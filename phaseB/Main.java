
/******************************************************
 *                                                    *
 * file:        Main.java                             *
 * Main file for cs-240b project 2020 (phase B)       *
 *                                                    *
 * @Author:     Apostolos Karampes                    *
 * @Version:    12-05-2020                            *
 * @email:      apskar@csd.uoc.gr                     *
 *                                                    *
 ******************************************************/

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Main {

	public static Flight DESTINATIONS[] = new Flight[10];
	int airCounter;
	static int counter = 0;
	public static Airline header = null;
	public static Client clients; /* global variable, pointer to the root of a tree of clients */
	public static priority_queue PQ = new priority_queue(
			0); /* global variable, pointer to a struct of type priority_queue */
	public static fifo_queue FQ; /* global variable, pointer to a struct of type fifo_queue */
	public static Client maxClient;
	static ArrayList<Client> clientsarray = new ArrayList<>();

	/**
	 * @brief Optional function to initialize data structures that need
	 *        initialization
	 *
	 * @return true on success false on failure
	 */
	public static boolean initialize() {

		for (int i = 0; i < 10; i++) {

			DESTINATIONS[i] = null;
		}
		return true;
	}

	/**
	 * @brief Register airline
	 *
	 * @param aid The airline's id
	 *
	 * @return 1 on success 0 on failure
	 */
	public static boolean register_airline(int aId) {
		Airline tmp;

		Airline newAirline = new Airline(aId);
		Airline current = Main.header;
		if (Main.header == null) {
			Main.header = newAirline;
			header.next = header;
			header.prev = header;
		} else {
			Airline last = header.prev;
			if (current.aId > newAirline.aId) {
				newAirline.next = header;
				newAirline.prev = last;
				last.next = header.prev = newAirline;
				header = newAirline;
			} else if (current.aId == newAirline.aId) {
				return false;
			} else {
				Airline currentAir = header;
				while (currentAir.next != header && currentAir.next.aId < newAirline.aId) {
					currentAir = currentAir.next;
				}

				if (currentAir.next == header) {
					newAirline.next = header;
					header.prev = newAirline;
					newAirline.prev = last;
					last.next = newAirline;
				} else {
					tmp = currentAir.next;
					currentAir.next = newAirline;
					newAirline.prev = currentAir;
					newAirline.next = tmp;
					tmp.prev = newAirline;
				}
			}
		}
		current = Main.header;
		System.out.print("Airlines= ");
		System.out.print("<" + current.aId + ">,");
		current = current.next;
		while (current != Main.header) {
			System.out.print("<" + current.aId + ">,");
			current = current.next;
		}

		System.out.println("\nDONE");
		return true;
	}

	/**
	 * @brief Insert new airplane to the list of the certain airline with aid
	 *
	 * @param pid         The airplanes id
	 * @param aid         The airlines id
	 * @param dest        Destination Id [0-9]
	 * @param depart_time Departure time
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean insert_airplane(int pId, int aId, int dest, int depart_time) {
		Airplane newAirplane = new Airplane(pId, dest, depart_time);
		Airline current = Main.header;
		Flight newFlight = new Flight(pId, depart_time);
		Flight tempflight = DESTINATIONS[newAirplane.dest];
		while (current.aId != aId) {
			current = current.next; // find the rc airline
		}
		Airplane temp = current.airplain;
		Airplane tempp = current.airplain;
		if (current.airplain == null) {
			current.airplain = newAirplane; // if header of planes is null put the first airplane
		}
		while (temp != null) {
			tempp = temp;
			if (newAirplane.pId < temp.pId) // if current airplane bigger than new go lc
				temp = temp.lc;
			else
				temp = temp.rc; // else go rc
		}
		if (tempp == null)
			tempp = newAirplane; // if no airplane put a new one
		else if (newAirplane.pId < tempp.pId)
			tempp.lc = newAirplane; // if its current bigger put it to the lc
		else
			tempp.rc = newAirplane; // else put it to the rc

		inorder_Airplanes(aId); // print airplanes

		Flight temppflight = null;
		if (DESTINATIONS[dest] == null) { // if header is null put the first flight
			DESTINATIONS[newAirplane.dest] = newFlight;
			DESTINATIONS[newAirplane.dest].depart_time = depart_time;
			DESTINATIONS[newAirplane.dest].pid = pId;
			DESTINATIONS[newAirplane.dest].lc = null; // the lc is null
			DESTINATIONS[newAirplane.dest].rc = null; // the rc is null
		} else {
			tempflight = DESTINATIONS[newAirplane.dest];
			while (tempflight != null) {
				if (newFlight.depart_time < tempflight.depart_time) {
					temppflight = tempflight;
					if (tempflight.thread_status == 1 || tempflight.thread_status == 3)
						tempflight = tempflight.lc;
					else
						break;
				} else if (newFlight.depart_time == tempflight.depart_time) {
					if (newFlight.pid < tempflight.pid) {
						temppflight = tempflight;
						if (tempflight.thread_status == 1 || tempflight.thread_status == 3)
							tempflight = tempflight.lc;
						else
							break;
					} else {
						temppflight = tempflight;
						if (tempflight.thread_status == 2 || tempflight.thread_status == 3)
							tempflight = tempflight.rc;
						else
							break;
					}
				} else {
					temppflight = tempflight;
					if (tempflight.thread_status == 2 || tempflight.thread_status == 3)
						tempflight = tempflight.rc;
					else
						break;
				}

			}
			if (temppflight == null) { // if the current flight is null put a flight with nno lifet,rc,children
				temppflight = newFlight;
				temppflight.lc = null;
				temppflight.rc = null;
			} else if (newFlight.depart_time < temppflight.depart_time) { // if current flight has bigger depart time
																			// then put
				// the new to the lc child of current
				tempflight = newFlight;
				tempflight.lc = temppflight.lc; // make lc of new the lc of currernt
				tempflight.rc = temppflight; // the rc of new looks at its parent(current flight)
				temppflight.lc = tempflight; // current's lc to new flight
				if (temppflight.thread_status == 3) // if current didnt have any child now has lc
					temppflight.thread_status = 2;
				else
					temppflight.thread_status = 0; // if it had a rc child now has two children
			} else if (newFlight.depart_time == temppflight.depart_time) {
				if (newFlight.pid < temppflight.pid) { // if current flight has bigger pid then put the new then sort
														// with pid
					tempflight = newFlight;
					tempflight.lc = temppflight.lc;
					tempflight.rc = temppflight;
					temppflight.lc = tempflight;
					if (temppflight.thread_status == 3)
						temppflight.thread_status = 2;
					else
						temppflight.thread_status = 0;
				} else {
					tempflight = newFlight;
					tempflight.lc = temppflight;
					tempflight.rc = temppflight.rc;
					temppflight.rc = tempflight;
					if (temppflight.thread_status == 3)
						temppflight.thread_status = 1;
					else
						temppflight.thread_status = 0;
				}

			} else {

				tempflight = newFlight; // else if depart time smaller then to the rc goes the new
				tempflight.lc = temppflight; // the lc of new looks to current where its its parent
				tempflight.rc = temppflight.rc; // the rc of new to the rc of its parent
				temppflight.rc = tempflight;

				if (temppflight.thread_status == 3)// if current didnt have any child now has rc
					temppflight.thread_status = 1;
				else
					temppflight.thread_status = 0; // else has 2 children
			}
		}

		inorder_destinations(dest); // print flight
		System.out.println("\nDONE");

		return true;
	}

	public static void inorder_destinations(int dest) {
		Flight root = DESTINATIONS[dest];
		System.out.print("\nDestination" + "<" + dest + ">=");
		if (root == null)
			System.out.print("");

		else if (root.thread_status == 3) {

			System.out.print("<" + root.pid + ":" + root.depart_time + ">" + ",");

		} else {
			Flight cur = lcMost(root);
			while (cur != null) {
				System.out.print("<" + cur.pid + ":" + cur.depart_time + ">" + ",");
				if (cur.thread_status != 2) // if has rc go rc
					cur = cur.rc;
				else if (cur.thread_status != 1)
					cur = lcMost(cur.rc); // else go lc
			}

		}

	}

	static Flight lcMost(Flight n) {
		if (n == null) // if its null return null
			return null;

		while (n.lc != null) // else until its not null go lc and return the lc
			n = n.lc;

		return n;
	}

	public static void inorder_Airplanes(int aId) {

		Airline current = header;
		while (current.aId != aId) {
			current = current.next;
		}
		System.out.print("\nAirline" + "<" + current.aId + ">=");

		Airplane root = current.airplain;
		if (root == null)
			System.out.print("");

		Stack<Airplane> s = new Stack<Airplane>();
		Airplane curr = root;
		while (curr != null || s.size() > 0) {
			/*
			 * Reach the lc most Airplane of the curr Airplane
			 */
			while (curr != null) {
				/*
				 * place pointer to a tree Airplane on the stack before traversing the
				 * Airplane's lc subtree
				 */
				s.push(curr);
				curr = curr.lc;
			}

			/* Current must be NULL at this point */
			curr = s.pop();

			System.out.print("<" + curr.pId + ":" + current.aId + ">" + ",");

			/*
			 * we have visited the Airplane and its lc subtree. Now, it's rc subtree's turn
			 */
			curr = curr.rc;
		}
		System.out.println();
	}

	/**
	 * @brief Cancel flight
	 *
	 * @param aid  The airlines id
	 * @param pid  The airplanes id
	 * @param dest Destination Id [0-9]
	 *
	 * @return true on success 0 on failure
	 */
	static boolean cancel_flight(int aid, int pid, int dest) {
		Airline current = Main.header;
		while (current.aId != aid) {
			current = current.next;
		}

		Airplane x = current.airplain;
		Airplane parent = x;
		while (x.pId != pid) {
			if (x.pId > pid) {
				parent = x;
				x = x.lc;
			} else if (x.pId < pid) {
				parent = x;
				x = x.rc;
			}
		}

		if (x == current.airplain && x.lc == null && x.rc == null) {
			current.airplain = null;
		}
		// case has no children
		else if (x.lc == null && x.rc == null) {
			if (parent.lc == x)
				parent.lc = null;
			else if (parent.rc == x)
				parent.rc = null;
			else
				x = null;
		} else if (x.lc != null && x.rc != null) {
			Airplane rc = x.rc;
			while (rc.lc != null) {
				x = rc.lc;
				rc.lc = x.rc;
			}
			Airplane tmp = x;
			int tmppid = tmp.pId;
			x.pId = tmppid;

		} else {
			Airplane child = null;
			if (x.lc != null) {
				child = x.lc;
			} else {
				child = x.rc;
			}
			if (x != current.airplain) {
				if (x == parent.lc)
					parent.lc = child;
				else
					parent.rc = child;
			} else
				current.airplain = child;

		}
		inorder_Airplanes(aid);
		System.out.println();
//		Flight root = DESTINATIONS[dest];
//		Flight x1 = DESTINATIONS[dest];
//		Flight parent1 = null;
//		while (x1.pid != pid) {
//			System.out.println();
//			parent1 = x1;
//			if (x1.pid < pid) {
//				if (x1.thread_status == 1 || x1.thread_status == 3)
//					x1 = x1.lc;
//				else
//					break;
//			} else {
//				if (x1.thread_status == 2 || x1.thread_status == 3)
//					x1 = x1.rc;
//				else
//					break;
//
//			}
//		}
//
//		if (x1 == DESTINATIONS[dest] && x1.lc == null && x1.rc == null) {
//			DESTINATIONS[dest] = null;
//		}
//
//		// case has no children
//		else if (x1.thread_status!=0) {
//			if (parent1.lc == x1)
//				parent1.lc = null;
//			else if (parent1.rc == x1)
//				parent1.rc = null;
//			else {
//				
//				parent1.lc=x1.rc;
//				parent1.rc=x1.lc;
//				x1 = null;
//			}
//		} else if (x1.lc != null && x1.rc != null) { //has children
//			Flight p2 = null;
//			while (x1.rc.lc != null) {
//				p2 = x1.rc;
//				x1 = x1.rc.lc;
//			}
//			p2.thread_status = 3;
//			Flight tmp = x1;
//			int tmppid = tmp.pid;
//			p2.rc = tmp;
//			x1.pid = tmppid;
//			x1.thread_status = 3;
//		} else {
//			Flight child = null;
//			if (x1.lc != null) {
//				child = x1.lc;
//			} else {
//				child = x1.rc;
//			}
//			if (x1 != root) {
//
//				if (x1 == parent1.lc) {
//					parent1.thread_status = 2;
//					parent1.lc = child;
//					child.rc = parent1;
//				} else {
//					parent1.thread_status = 1;
//					child.lc = parent1;
//					parent1.rc = child;
//				}
//			} else {
//				DESTINATIONS[dest] = child;
//				DESTINATIONS[dest].thread_status = 3;
//				DESTINATIONS[dest].lc = null;
//				DESTINATIONS[dest].rc = null;
//			}
//		}
		inorder_destinations(dest);
		System.out.println("\nDONE");
		return true;
	}

	/**
	 * @brief Delete airline
	 *
	 * @param aid The airlines id
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean delete_airline(int aId) {
		Airline current = Main.header;
		while (current.aId != aId) {
			current = current.next;
		}
		Airline tmp = current;

//		while (tmp.airplain != null) {
//			Flight root = DESTINATIONS[tmp.airplain.dest];
//			Flight x1 = root;
//			Flight parent1 = x1;
//			while (x1.pid != tmp.airplain.pId) {
//				if (x1.pid > tmp.airplain.pId) {
//					parent1 = x1;
//					x1 = x1.lc;
//				} else if (x1.pid < tmp.airplain.pId) {
//					parent1 = x1;
//					x1 = x1.rc;
//				}
//			}
//			if (x1 == DESTINATIONS[tmp.airplain.dest] && x1.lc == null && x1.rc == null) {
//				DESTINATIONS[tmp.airplain.dest] = null;
//			}
//			// case has no children
//			else if (x1.lc == null && x1.rc == null) {
//				if (parent1.lc == x1)
//					parent1.lc = null;
//				else if (parent1.rc == x1)
//					parent1.rc = null;
//				else
//					x1 = null;
//			} else if (x1.lc != null && x1.rc != null) {
//				Flight p2 = null;
//				while (x1.rc.lc != null) {
//					p2 = x1.rc;
//					x1 = x1.rc.lc;
//				}
//				p2.thread_status = 3;
//				Flight tmp1 = x1;
//				int tmppid = tmp1.pid;
//				p2.rc = tmp1;
//				x1.pid = tmppid;
//				x1.thread_status = 3;
//			} else {
//				Flight child = null;
//				if (x1.lc != null) {
//					child = x1.lc;
//				} else {
//					child = x1.rc;
//				}
//				if (x1 != root) {
//
//					if (x1 == parent1.lc) {
//						parent1.thread_status = 2;
//						parent1.lc = child;
//						child.rc = parent1;
//					} else {
//						parent1.thread_status = 1;
//						child.lc = parent1;
//						parent1.rc = child;
//					}
//				} else {
//					DESTINATIONS[tmp.airplain.dest] = child;
//					DESTINATIONS[tmp.airplain.dest].thread_status = 3;
//					DESTINATIONS[tmp.airplain.dest].lc = null;
//					DESTINATIONS[tmp.airplain.dest].rc = null;
//				}
//			}
//			if (tmp.airplain.lc != null)
//				tmp.airplain = tmp.airplain.lc;
//			else
//				tmp.airplain = tmp.airplain.rc;
//		}

		// deleting tree
		if (current.airplain != null) {
			Queue<Airplane> deleteq = new LinkedList<Airplane>();

			// Do level order traversal starting from root
			deleteq.add(current.airplain);
			while (!deleteq.isEmpty()) {

				Airplane nodeair = deleteq.peek();
				deleteq.poll();
				if (nodeair.lc != null)
					deleteq.add(nodeair.lc);
				if (nodeair.rc != null)
					deleteq.add(nodeair.rc);

			}
		}
		if (current.airplain != null) {
			current.airplain = null;
		}

		Airline next = current.next;
		Airline prev = current.prev;

		if (next == header) {
			header = null;
		}
		if (current == header) {
			prev = header.next;
			header = header.next;
			prev.next = header;
			header.prev = prev;
		} else if (next == header) {
			prev.next = header;
			header.prev = prev;
		} else {

			prev.next = next;
			next.prev = prev;
		}

		System.out.println("Airlines");

		print_airlines();
		System.out.print("Destinations");

		print_destinations();
		return true;

	}

	/**
	 * @brief Acquisition airline
	 *
	 * @param aid1 The first airline id
	 * @param aid2 The second airline id
	 *
	 * @return true on success 0 on failure
	 */
	private static boolean acquisition_airline(int aid1, int aid2) {
		Airline current1 = Main.header;
		Airline current2 = Main.header;
		Airline found1 = null;
		Airline found2 = null;
		while (current1.aId != aid1) {
			current1 = current1.next;
		}
		if (current1.aId == aid1) {
			found1 = current1;
		}

		while (current2.aId != aid2) {
			current2 = current2.next;
		}
		if (current2.aId == aid2) {
			found2 = current2;
		}
		Airplane temp = found2.airplain;
		if (found1 != null && found2 != null) {
			traversal(found1.airplain, temp);
		}
		Airline next = found1.next;
		Airline prev = found1.prev;
		if (found1 == header) {
			header = null;
		}
		if (found1 == header) {
			prev = header.next;
			header = header.next;
			prev.next = header;
			header.prev = prev;
		} else if (next == header) {
			prev.next = header;
			header.prev = prev;
		} else {

			prev.next = next;
			next.prev = prev;
		}

		print_airlines();
		return true;
	}

	static Airplane traversal(Airplane node, Airplane temp) {
		if (node == null)
			return temp;

		if (temp == null) {
			temp = node;
		} else {
			if (node.pId < temp.pId) {
				temp.lc = traversal(node.lc, temp.lc);
			} else {
				temp.rc = traversal(node.rc, temp.rc);
			}
		}
		return temp;
	}

	/**
	 * @brief Subsidiary company
	 *
	 * @param aid1 The first airline id
	 * @param aid2 The second airline id
	 * @param dest Destination
	 *
	 * @return true on success 0 on failure
	 */
	private static boolean subsidiary_airiline(int aid1, int aid2, int dest) {
		Airline current1 = Main.header;
		Airline current2 = Main.header;
		Airline found1 = null;
		Airline found2 = null;
		while (current1.aId != aid1) {
			current1 = current1.next;
		}
		if (current1.aId == aid1) {
			found1 = current1;
		}

		while (current2.aId != aid2) {
			current2 = current2.next;
		}
		if (current2.aId == aid2) {
			found2 = current2;
		}
		Airplane temp = found2.airplain;
		if (found1 != null && found2 != null) {
			traversalforS(found1.airplain, temp, dest);
		}

		print_airlines();
		return true;

	}

	static Airplane traversalforS(Airplane node, Airplane temp, int dest) {
		if (node == null)
			return temp;
		if (node.dest == dest) {
			if (temp == null) {
				temp = node;
			} else {
				if (node.pId < temp.pId) {
					temp.lc = traversalforS(node.lc, temp.lc, dest);
				} else {
					temp.rc = traversalforS(node.rc, temp.rc, dest);
				}
			}
		}
		return temp;
	}

	/**
	 * @brief Find flight
	 *
	 * @param dest Destination
	 * @param ts   Timestamp
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean find_flight(int dest, int ts) {
		System.out.print("Destination=");
		Flight tmp = DESTINATIONS[dest];
		while (tmp != null) {
			if (tmp.depart_time == ts)
				break;
			if (tmp.depart_time < ts) {
				if (tmp.thread_status == 1 || tmp.thread_status == 3)
					tmp = tmp.lc;
				else
					break;
			} else if (tmp.depart_time > ts) {
				if (tmp.thread_status == 2 || tmp.thread_status == 3)
					tmp = tmp.rc;
				else
					break;
			}
		}

		while (tmp != null) {
			if (ts < tmp.depart_time) {
				System.out.print("<" + tmp.pid + ":" + tmp.depart_time + ">");
			}
			tmp = tmp.rc;
		}
		System.out.println();
		return true;
	}

	/**
	 * @brief New client
	 *
	 * @param cid The client id
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean new_client(int cid) {
		Client newclient = new Client(cid, 0);
		int tmp = 0;

		if (PQ.Q == null) {
			PQ.Q = newclient;
			clientsarray.add(newclient);
			PQ.num_elements++;
		} else {
			clientsarray.add(newclient);
			PQ.num_elements++;
			Client root = PQ.Q;
			Client parent = PQ.Q;
			Stack<Integer> s = new Stack<Integer>();
			tmp = PQ.num_elements;

			while (tmp != 1) {
				s.push(tmp % 2);
				tmp = tmp / 2;

			}
			while (s.isEmpty() == false) {
				tmp = s.pop();
				parent = root;

				if (tmp == 0 && root.lc != null)
					root = root.lc;
				else if (tmp != 0 && root.mc != null)
					root = root.mc;
				else
					root = root.rc;
			}
			if (tmp == 0)
				parent.lc = newclient;
			else if (parent.mc == null)
				parent.mc = newclient;
			else
				parent.rc = newclient;
		}
		print_clients();

		return true;
	}

	/**
	 * @brief Erase client
	 *
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean erase_client() {
		Client current = PQ.Q;
		maxClient = current;
		findMaxClient(current);
		if (maxClient == PQ.Q) {
			PQ.Q = PQ.Q.mc;
			PQ.Q.lc = maxClient.lc;
			PQ.Q.rc = maxClient.rc;
			PQ.Q.mc = maxClient.mc.lc;
		} else if (maxClient != PQ.Q) {
			maxClient = null;
		}
		print_clients();
		return true;
	}

	static void findMaxClient(Client node) {
		if (node == null)
			return;
		if (node.miles > maxClient.miles) {
			maxClient = node;
		}
		findMaxClient(node.lc);
		findMaxClient(node.mc);
		findMaxClient(node.rc);
	}

	/**
	 * @brief Travel client
	 *
	 * @param cid  The client id
	 * @param dest Destination
	 *
	 * @return true on success 0 on failure
	 */
	private static boolean travel_client(int cid, int dest) {
		Client node = PQ.Q;
		int miles = (dest + 1) * 100;
		insertmiles(node, cid, miles);
		print_clients();
		return true;
	}

	static void insertmiles(Client node, int cid, int miles) {
		if (node == null)
			return;
		if (node.cid == cid) {
			node.miles = miles;
		}
		insertmiles(node.lc, cid, miles);
		insertmiles(node.mc, cid, miles);
		insertmiles(node.rc, cid, miles);
	}

	/**
	 * @brief Print airlines
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean print_airlines() {
		Airline current = header;
		Airplane cc = header.airplain;
		System.out.print("Airline" + "<" + current.aId + ">=");
		if (cc == null)
			System.out.println("");

		Stack<Airplane> s = new Stack<Airplane>();
		Airplane curr = cc;
		while (curr != null || s.size() > 0) {

			/*
			 * Reach the lc most Airplane of the curr Airplane
			 */
			while (curr != null) {
				/*
				 * place pointer to a tree Airplane on the stack before traversing the
				 * Airplane's lc subtree
				 */
				s.push(curr);
				curr = curr.lc;
			}

			/* Current must be NULL at this point */
			curr = s.pop();

			System.out.print("<" + curr.pId + ":" + header.aId + ">" + ",");

			/*
			 * we have visited the Airplane and its lc subtree. Now, it's rc subtree's turn
			 */
			curr = curr.rc;
		}

		current = current.next;
		while (current != header) {
			System.out.print("Airline" + "<" + current.aId + ">=");
			Airplane root = current.airplain;
			if (root == null)
				System.out.println("");
			else {
				Stack<Airplane> s1 = new Stack<Airplane>();
				Airplane curr1 = root;
				while (curr1 != null || s1.size() > 0) {
					while (curr1 != null) {
						s1.push(curr1);
						curr1 = curr1.lc;
					}
					curr1 = s1.pop();

					System.out.print("<" + curr1.pId + ":" + current.aId + ">" + ",");
					curr1 = curr1.rc;
				}

				System.out.println();

			}

			current = current.next;
		}

		System.out.println("DONE");
		return true;
	}

	/**
	 * @brief Print destinations
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean print_destinations() {
		for (int i = 1; i < Main.DESTINATIONS.length; i++) {
			inorder_destinations(i);
		}
		System.out.println("\nDONE");
		return true;
	}

	/**
	 * @brief Print clients
	 *
	 * @return true on success 0 on failure
	 */
	private static boolean print_clients() {

		Client current = PQ.Q;
		System.out.print("Priority Queue=");

		Client root = PQ.Q;
		if (root == null)
			System.out.print("");

		for (int i = 0; i < clientsarray.size(); i++) {
			System.out.print("<" + clientsarray.get(i).cid + ":" + clientsarray.get(i).miles + ">" + ",");
		}
		System.out.println("\nDONE");
		return true;
	}

	/**
	 * @brief The main function
	 *
	 * @param argc Number of arguments
	 * @param argv Argument vector
	 *
	 * @return 0 on success 1 on failure
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		BufferedReader inputFile; /* Input file */
		String line; /* Data for eachline of the input file */
		String[] params; /* Event parameters arguments */

		/* Check command buff arguments */
		if (args.length != 1) {
			System.err.println("Usage: <executable-name> <input_file>");
			System.exit(0);
		}

		/* Open input file */
		inputFile = new BufferedReader(new FileReader(args[0]));

		/* Read input file and handle the events */
		while ((line = inputFile.readLine()) != null) {

			if (line.length() == 0)
				continue;

			System.out.println(">>> Event: " + line);
			params = line.split(" ");
			char eventID = line.charAt(0);

			switch (eventID) {

			/* Comment */
			case '#':
				break;

			/*
			 * Register airline R <aid>
			 */
			case 'R': {
				int aid = Integer.parseInt(params[1]);

				if (register_airline(aid)) {
					System.out.println("R succeeded");
				} else {
					System.err.println("R failed");
				}

				break;
			}

			/*
			 * Insert new airplane I <pid> <aid> <dest> <depart_time>
			 */
			case 'I': {
				int pid = Integer.parseInt(params[1]);
				int aid = Integer.parseInt(params[2]);
				int dest = Integer.parseInt(params[3]);
				int depart_time = Integer.parseInt(params[4]);

				if (insert_airplane(pid, aid, dest, depart_time)) {
					System.out.println("I succeeded");
				} else {
					System.err.println("I failed");
				}

				break;
			}

			/*
			 * Cancel flight C <aid> <pid> <dest>
			 */
			case 'C': {
				int aid = Integer.parseInt(params[1]);
				int pid = Integer.parseInt(params[2]);
				int dest = Integer.parseInt(params[3]);

				if (cancel_flight(aid, pid, dest)) {
					System.out.println("C succeeded");
				} else {
					System.err.println("C failed");
				}

				break;
			}

			/*
			 * Delete airline D <aid>
			 */
			case 'D': {
				int aid = Integer.parseInt(params[1]);

				if (delete_airline(aid)) {
					System.out.println("D succeeded");
				} else {
					System.err.println("D failed");
				}

				break;
			}

			/*
			 * Acquisition airline A <aid1> <aid2>
			 */
			case 'A': {
				int aid1 = Integer.parseInt(params[1]);
				int aid2 = Integer.parseInt(params[2]);

				if (acquisition_airline(aid1, aid2)) {
					System.out.println("A succeeded");
				} else {
					System.err.println("A failed");
				}

				break;
			}

			/*
			 * Subsidiary company S <aid1> <aid2> <dest>
			 */
			case 'S': {
				int aid1 = Integer.parseInt(params[1]);
				int aid2 = Integer.parseInt(params[2]);
				int dest = Integer.parseInt(params[3]);

				if (subsidiary_airiline(aid1, aid2, dest)) {
					System.out.println("S succeeded");
				} else {
					System.err.println("S failed");
				}

				break;
			}

			/*
			 * Find flight F <dest> <ts>
			 */
			case 'F': {
				int dest = Integer.parseInt(params[1]);
				int ts = Integer.parseInt(params[2]);

				if (find_flight(dest, ts)) {
					System.out.println("F succeeded");
				} else {
					System.err.println("F failed");
				}

				break;
			}

			/*
			 * New client N <cid>
			 */
			case 'N': {
				int cid = Integer.parseInt(params[1]);

				if (new_client(cid)) {
					System.out.println("N succeeded");
				} else {
					System.err.println("N failed");
				}

				break;
			}

			/*
			 * Erase client E
			 */
			case 'E': {
				if (erase_client()) {
					System.out.println("E succeeded");
				} else {
					System.err.println("E failed");
				}

				break;
			}

			/*
			 * Travel client T <cid> <dest>
			 */
			case 'T': {
				int cid = Integer.parseInt(params[1]);
				int dest = Integer.parseInt(params[2]);

				if (travel_client(cid, dest)) {
					System.out.println("T succeeded");
				} else {
					System.err.println("T failed");
				}

				break;
			}

			/*
			 * Print airlines X
			 */
			case 'X': {
				if (print_airlines()) {
					System.out.println("X succeeded");
				} else {
					System.err.println("X failed");
				}

				break;
			}

			/*
			 * Print destinations Y
			 */
			case 'Y': {
				if (print_destinations()) {
					System.out.println("Y succeeded");
				} else {
					System.err.println("Y failed");
				}

				break;
			}

			/*
			 * Print clients Z
			 */
			case 'Z': {
				if (print_clients()) {
					System.out.println("Z succeeded");
				} else {
					System.err.println("Z failed");
				}

				break;
			}

			/* Empty line */
			case '\n':
				break;

			/* Ignore everything else */
			default:
				System.out.println("Ignoring " + line);
				break;

			}

		}

	}

}
