
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static Destinations DESTINATIONS[] = new Destinations[10];
	int airCounter;
	public static Airline header = null;

	public static boolean initialize() {

		for (int i = 0; i < 10; i++) {
			DESTINATIONS[i] = new Destinations(null, null);
			DESTINATIONS[i].sentinel_p = new Flights(0, 0);
		}
		return true;
	}

	/**
	 * @brief Register airline
	 *
	 * @param aId The airline's id
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
				while (currentAir.next!= header && currentAir.next.aId < newAirline.aId) {
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
	 * @brief Insert new airplane to the list of the certain airline with aId
	 *
	 * @param aId         The airlines id
	 * @param pId         The airplanes id
	 * @param dest        Destination Id [0-9]
	 * @param depart_time Departure time
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean insert_airplanes(int aId, int pId, int dest, int depart_time) {

		Airplane newAirplane = new Airplane(pId, dest, depart_time);
		Airline current = Main.header;
		Airplane tmp = Main.header.airplain;
		Flights newFlight = new Flights(pId, depart_time);
		Flights sentinel = null;
		while (current.aId != aId) {
			current = current.next;
		}
		if (current.airplain == null) {
			current.airplain = newAirplane;
			current.airplain.dest = newAirplane.dest;
			current.airplain.depart_time = newAirplane.depart_time;

		} else if (current.airplain.pId > newAirplane.pId) {
			current.airplain.prev = newAirplane;
			newAirplane.next = current.airplain;
			current.airplain = newAirplane;
			current.airplain.dest = newAirplane.dest;
			current.airplain.depart_time = newAirplane.depart_time;

		} else if (current.airplain.pId == newAirplane.pId) {
			System.out.println("already exists");
		} else {
			Airplane currentAir = current.airplain;
			while (currentAir.next != null && currentAir.next.pId < newAirplane.pId) {
				currentAir = currentAir.next;
			}
			if (currentAir.next != null) {
				tmp = currentAir.next;
				currentAir.next = newAirplane;
				currentAir.next.dest = newAirplane.dest;
				newAirplane.next = tmp;
				newAirplane.prev = currentAir;

			} else {
				currentAir.next = newAirplane;
				newAirplane.prev = currentAir;
				currentAir.dest = newAirplane.dest;
				current.airplain.depart_time = newAirplane.depart_time;
			}
		}

		Flights tmpf = Main.DESTINATIONS[newAirplane.dest].flight_p;
		Flights flighttmp = null;
		Flights prev = null;

		if (Main.DESTINATIONS[newAirplane.dest].flight_p == null) {
			Main.DESTINATIONS[newAirplane.dest].flight_p = newFlight;
			newFlight.depart_time = newAirplane.depart_time;
			newFlight.pId = newAirplane.pId;
			Main.DESTINATIONS[newAirplane.dest].sentinel_p.depart_time = newFlight.depart_time;
			Main.DESTINATIONS[newAirplane.dest].sentinel_p.pId = newFlight.pId;
			newFlight.next = Main.DESTINATIONS[newAirplane.dest].sentinel_p;
		} else if (Main.DESTINATIONS[newAirplane.dest].flight_p.depart_time == newFlight.depart_time) {
			Flights fl = Main.DESTINATIONS[newAirplane.dest].flight_p;
			while (fl != Main.DESTINATIONS[newAirplane.dest].sentinel_p && fl.pId < newFlight.pId) {
				fl = fl.next;
			}
			if (fl != Main.DESTINATIONS[newAirplane.dest].sentinel_p) {
				if (fl.next != null) {
					prev = fl.next;
					flighttmp = fl.next;
					fl.next = newFlight;
					newFlight.next = flighttmp;
					newFlight.depart_time = newAirplane.depart_time;
					newFlight.pId = newAirplane.pId;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.depart_time = newFlight.depart_time;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.pId = newFlight.pId;
					flighttmp.next = Main.DESTINATIONS[newAirplane.dest].sentinel_p;
					prev = fl;
				} else {
					flighttmp = fl.next;
					fl.next = newFlight;
					newFlight.next = flighttmp;
					newFlight.depart_time = newAirplane.depart_time;
					newFlight.pId = newAirplane.pId;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.depart_time = newFlight.depart_time;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.pId = newFlight.pId;
					flighttmp.next = Main.DESTINATIONS[newAirplane.dest].sentinel_p;
				}
			} else {
				if (fl.next != null) {
					prev = fl.next;
					fl.next = newFlight;
					newFlight.depart_time = newAirplane.depart_time;
					newFlight.pId = newAirplane.pId;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.depart_time = newFlight.depart_time;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.pId = newFlight.pId;
					newFlight.next = Main.DESTINATIONS[newAirplane.dest].sentinel_p;

				} else {
					fl.next = newFlight;
					prev = fl;
					newFlight.depart_time = newAirplane.depart_time;
					newFlight.pId = newAirplane.pId;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.depart_time = newFlight.depart_time;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.pId = newFlight.pId;
					newFlight.next = Main.DESTINATIONS[newAirplane.dest].sentinel_p;
				}
			}

		} else {

			Flights fl = tmpf;
			while (fl.next != Main.DESTINATIONS[newAirplane.dest].sentinel_p
					&& fl.next.depart_time < newFlight.depart_time) {
				fl = fl.next;
			}
			if (fl != Main.DESTINATIONS[newAirplane.dest].sentinel_p) {
				if (fl.next != null) {
					prev = fl.next;
					flighttmp = fl.next;
					fl.next = newFlight;
					newFlight.next = flighttmp;
					newFlight.depart_time = newAirplane.depart_time;
					newFlight.pId = newAirplane.pId;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.depart_time = newFlight.depart_time;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.pId = newFlight.pId;
					flighttmp.next = Main.DESTINATIONS[newAirplane.dest].sentinel_p;
					prev = fl;
				} else {
					flighttmp = fl.next;
					fl.next = newFlight;
					newFlight.next = flighttmp;
					newFlight.depart_time = newAirplane.depart_time;
					newFlight.pId = newAirplane.pId;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.depart_time = newFlight.depart_time;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.pId = newFlight.pId;
					flighttmp.next = Main.DESTINATIONS[newAirplane.dest].sentinel_p;
				}
			} else {
				if (fl.next != null) {
					prev = fl.next;
					fl.next = newFlight;
					newFlight.depart_time = newAirplane.depart_time;
					newFlight.pId = newAirplane.pId;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.depart_time = newFlight.depart_time;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.pId = newFlight.pId;
					newFlight.next = Main.DESTINATIONS[newAirplane.dest].sentinel_p;

				} else {
					fl.next = newFlight;
					prev = fl;
					newFlight.depart_time = newAirplane.depart_time;
					newFlight.pId = newAirplane.pId;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.depart_time = newFlight.depart_time;
					Main.DESTINATIONS[newAirplane.dest].sentinel_p.pId = newFlight.pId;
					newFlight.next = Main.DESTINATIONS[newAirplane.dest].sentinel_p;
				}
			}
		}
		Airline currenttt = Main.header;
		int i = 1;
		Airplane cc = header.airplain;

		System.out.print("Airline" + i + " = ");

		while (cc != null) {
			System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

			cc = cc.next;
		}
		i = 2;
		currenttt = currenttt.next;
		System.out.println();
		while (currenttt != Main.header) {
			cc = currenttt.airplain;
			if (cc != null) {
				System.out.print("Airline" + i + " = ");

				while (cc != null) {
					System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

					cc = cc.next;
				}
				System.out.println();
			}
			currenttt = currenttt.next;
			i++;

		}

		System.out.println("DONE");
		return true;
	}

	/**
	 * @brief Cancel flight
	 *
	 * @param aId  The airlines id
	 * @param pId  The airplanes id
	 * @param dest Destination Id [0-9]
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean cancel_flight(int aId, int pId, int dest) {
		Airline current = Main.header;
		Airplane tmp = Main.header.airplain;
		Airplane temp = Main.header.airplain;
		while (current.aId != aId) {
			current = current.next;
		}
		temp = current.airplain;
		while (temp.pId != pId) {
			temp = temp.next;
		}

		Flights f = DESTINATIONS[dest].flight_p;
		Flights f1 = DESTINATIONS[dest].flight_p;

		if (DESTINATIONS[dest].flight_p.pId == pId) {
			DESTINATIONS[dest].flight_p = f.next;
			f = null;
		} else {
			while (f.pId != pId && f.next != DESTINATIONS[dest].sentinel_p) {
				f1 = f;
				f = f.next;
			}
			if (f.next == DESTINATIONS[dest].sentinel_p) {
				f1.next = DESTINATIONS[dest].sentinel_p;
				f = null;
			} else {
				f1.next = f.next;
				f = null;
			}
		}

		tmp = temp;
		if (tmp.pId == pId) {
			if (tmp.prev == null) {
				tmp = tmp.next;
				if (tmp != null) {
					tmp.prev = null;
				}
				current.airplain = tmp;
			} else if (tmp.next != null) {
				tmp.prev.next = tmp.next;
				tmp.next.prev = tmp.prev;
			} else {
				tmp = tmp.prev;
				tmp.next = null;
			}

		}

		Airline currenttt = Main.header;
		int i = 1;
		Airplane cc = header.airplain;

		System.out.print("Airline" + i + " = ");

		while (cc != null) {
			System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

			cc = cc.next;
		}
		i = 2;
		currenttt = currenttt.next;
		System.out.println();
		while (currenttt != Main.header) {
			cc = currenttt.airplain;
			if (cc != null) {
				System.out.print("Airline" + i + " = ");

				while (cc != null) {
					System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

					cc = cc.next;
				}
				System.out.println();
			}
			currenttt = currenttt.next;
			i++;

		}

		System.out.println("DONE");

		return true;
	}

	/**
	 * @brief Delete airline
	 *
	 * @param aId The airlines id
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean delete_airline(int aId) {
		Airline current = Main.header;
		while (current.aId != aId) {
			current = current.next;
		}

		while (current.airplain != null) {

			Flights f = DESTINATIONS[current.airplain.dest].flight_p;
			Flights f1 = DESTINATIONS[current.airplain.dest].flight_p;

			if (DESTINATIONS[current.airplain.dest].flight_p.pId == current.airplain.pId) {
				DESTINATIONS[current.airplain.dest].flight_p = f.next;
				f = null;
			} else {
				while (f.pId != current.airplain.pId && f.next != DESTINATIONS[current.airplain.dest].sentinel_p) {
					f1 = f;
					f = f.next;
				}
				if (f.next == DESTINATIONS[current.airplain.dest].sentinel_p) {
					f1.next = DESTINATIONS[current.airplain.dest].sentinel_p;
					f = null;
				} else {
					f1.next = f.next;
					f = null;
				}
			}

			current.airplain = current.airplain.next;
		}
		Airline tmp = current;
		if (tmp == header) {
			if (tmp.next != header) {
				tmp.next.prev = tmp.prev;
				tmp.prev.next = tmp.next;
				header = tmp.next;
			} else {
				header = null;
			}
		} else {
			tmp.next.prev = tmp.prev;
			tmp.prev.next = tmp.next;
		}

		current = Main.header;
		System.out.print("Airlines= ");
		if (current != null) {
			System.out.print("<" + current.aId + ">,");
			current = current.next;
		}
		while (current != Main.header) {
			System.out.print("<" + current.aId + ">,");
			current = current.next;
		}

		System.out.println("\nDONE");
		return true;

	}

	/**
	 * @brief Acquisition airline
	 *
	 * @param aId1 The first airline id
	 * @param aId2 The second airline id
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean acquisition_airline(int aId1, int aId2) {
		Airline current1 = header;
		Airline current2 = header;
		Airline found1 = null;
		Airline found2 = null;
		while (current1.aId != aId1) {
			if (current1.aId != aId1) {
				break;
			} else {
				current1 = current1.next;
				if (current1 == header) {
					return false;
				}
			}
			current1 = current1.next;
		}
		if (current1.aId == aId1) {
			found1 = current1;

		}

		while (current2.aId != aId2) {
			if (current2.aId != aId2) {
				break;
			} else {
				current2 = current2.next;
				if (current2 == header) {
					return false;
				}
			}
			current2 = current2.next;
		}
		if (current2.aId == aId2) {
			found2 = current2;

		}
		Airplane tmp = null;

		if (found1 != null && found2 != null) {
			Airplane root2 = found2.airplain;
			Airplane currentAir = root2;
			Airplane root1 = found1.airplain;
			while (root1 != null) {
				if (root2 == null) {
					root2 = root1;
				} else if (root2.pId > root1.pId) {
					root2.prev=root1;
					root1.next = root2;
					root2 = root1;
				} else {
					currentAir = root2;
					while (currentAir.next != null && currentAir.next.pId < found1.airplain.pId) {
						currentAir = currentAir.next;
					}
					if (currentAir.next != null) {
						tmp = currentAir.next;
						currentAir.next = found1.airplain;
						found1.airplain.next = tmp;
						root1.prev=currentAir;

					} else {
						currentAir.next = root1;
						root1.prev=currentAir;
					}
				}

				root1 = root1.next;
			}

			if (found1 != null) {
				if (found1 == header) {
					if (found1.next != header) {
						found1.next.prev = found1.prev;
						found1.prev.next = found1.next;
						header = found1.next;
					} else {
						header = null;
					}
				} else {
					found1.next.prev = found1.prev;
					found1.prev.next = found1.next;
				}
			}
		}
		Airline currenttt = Main.header;
		int i = 1;
		Airplane cc = header.airplain;

		System.out.print("Airline" + currenttt.aId + " = ");

		while (cc != null) {
			System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

			cc = cc.next;
		}
		i = 2;
		currenttt = currenttt.next;
		System.out.println();
		while (currenttt != Main.header) {
			cc = currenttt.airplain;
			if (cc != null) {
				System.out.print("Airline" + i + " = ");

				while (cc != null) {
					System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

					cc = cc.next;
				}
				System.out.println();
			}
			currenttt = currenttt.next;
			i++;

		}

		System.out.println("DONE");

		return true;
	}

	/**
	 * @brief Subsidiary company
	 *
	 * @param aId1 The first airline id
	 * @param aId2 The second airline id
	 * @param dest Destination
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean subsidiary_airiline(int aId1, int aId2, int dest) {
		Airline current = Main.header;
		Airline current1 = Main.header;
		Airline current2 = Main.header;
		Airline found1 = null;
		Airline found2 = null;
		while (current1.aId != aId1) {
			if (current1.aId != aId1) {
				break;
			} else {
				current1 = current1.next;
				if (current1 == header) {
					return false;
				}
			}
			current1 = current1.next;
		}
		if (current1.aId == aId1) {
			found1 = current1;
		}

		while (current2.aId != aId2) {
			if (current2.aId != aId2) {
				break;
			} else {
				current2 = current2.next;
				if (current2 == header) {
					return false;
				}
			}
			current2 = current2.next;
		}
		if (current2.aId == aId2) {
			found2 = current2;
		}
		Airplane tmp = null;
		if (found1 != null && found2 != null) {
			Airplane root2 = found2.airplain;
			Airplane root1 = found1.airplain;
			Airplane currentAir = root2;
			while (root1 != null) {
				if (root1.dest == dest) {
					if (root2 == null) {
						root2 = root1;
					} else if (root2.pId > root1.pId) {
						root2.prev=root1;
						root1.next = root2;
						root2 = root1;
					} else {
						currentAir = root2;
						while (currentAir.next != null && currentAir.next.pId < found1.airplain.pId) {
							currentAir = currentAir.next;
						}
						if (currentAir.next != null) {
							tmp = currentAir.next;
							currentAir.next = found1.airplain;
							found1.airplain.next = tmp;
							root1.prev=currentAir;

						} else {
							currentAir.next = root1;
							root1.prev=currentAir;
							
						}
					}
				}
				root1 = root1.next;
			}

		}

		Airline currenttt = Main.header;
		int i = 1;
		Airplane cc = header.airplain;

		System.out.print("Airline" + i + " = ");

		while (cc != null) {
			System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

			cc = cc.next;
		}
		i = 2;
		currenttt = currenttt.next;
		System.out.println();
		while (currenttt != Main.header) {
			cc = currenttt.airplain;
			if (cc != null) {
				System.out.print("Airline" + i + " = ");

				while (cc != null) {
					System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

					cc = cc.next;
				}
				System.out.println();
			}
			currenttt = currenttt.next;
			i++;

		}

		System.out.println("DONE");
		return true;
	}

	/**
	 * @brief Partition airplanes
	 *
	 * @param aId The airline id
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean partition_airplanes(int aId1) {
		Airline current = Main.header;
		Airline now = header;

		while (current.aId != aId1) {
			if (current.aId != aId1) {
				break;
			} else {
				current = current.next;
				if (current == header) {
					return false;
				}
			}
			current = current.next;
		}

		while (current.airplain != null) {
			Airline left = current.prev;
			Airline right = current.next;
			Airplane newAirplane = new Airplane(current.airplain.pId, current.airplain.dest,
					current.airplain.depart_time);
			Airplane tmp = Main.header.airplain;
			while (current.aId != right.aId) {
				if (current.aId != right.aId) {
					break;
				} else {
					current = current.next;
					if (current == header) {
						return false;
					}
				}
				current = current.next;
			}
			if (current.airplain == null) {
				current.airplain = newAirplane;
				current.airplain.dest = newAirplane.dest;
				current.airplain.depart_time = newAirplane.depart_time;

			} else if (current.airplain.pId > newAirplane.pId) {
				current.airplain.prev = newAirplane;
				newAirplane.next = current.airplain;
				current.airplain = newAirplane;
				current.airplain.dest = newAirplane.dest;
				current.airplain.depart_time = newAirplane.depart_time;

			} else if (current.airplain.pId == newAirplane.pId) {
				System.out.println("already exists");
			} else {
				Airplane currentAir = current.airplain;
				while (currentAir.next != null && currentAir.next.pId < newAirplane.pId) {
					currentAir = currentAir.next;
				}
				if (currentAir.next != null) {
					tmp = currentAir.next;
					currentAir.next = newAirplane;
					currentAir.next.dest = newAirplane.dest;
					newAirplane.next = tmp;
					newAirplane.prev = currentAir;

				} else {
					currentAir.next = newAirplane;
					newAirplane.prev = currentAir;
					currentAir.dest = newAirplane.dest;
					current.airplain.depart_time = newAirplane.depart_time;

				}
			}

			current.airplain = current.airplain.next;
			if (current.airplain != null) {
				newAirplane = new Airplane(current.airplain.pId, current.airplain.dest, current.airplain.depart_time);
				tmp = Main.header.airplain;

				while (current.aId != left.aId) {
					if (current.aId != left.aId) {
						break;
					} else {
						current = current.prev;
						if (current == header) {
							return false;
						}
					}
					current = current.prev;
				}
				if (current.airplain == null) {
					current.airplain = newAirplane;
					current.airplain.dest = newAirplane.dest;
					current.airplain.depart_time = newAirplane.depart_time;

				} else if (current.airplain.pId > newAirplane.pId) {
					current.airplain.prev = newAirplane;
					newAirplane.next = current.airplain;
					current.airplain = newAirplane;
					current.airplain.dest = newAirplane.dest;
					current.airplain.depart_time = newAirplane.depart_time;

				} else if (current.airplain.pId == newAirplane.pId) {
					System.out.println("already exists");
				} else {
					Airplane currentAir = current.airplain;
					while (currentAir.next != null && currentAir.next.pId < newAirplane.pId) {
						currentAir = currentAir.next;
					}
					if (currentAir.next != null) {
						tmp = currentAir.next;
						currentAir.next = newAirplane;
						currentAir.next.dest = newAirplane.dest;
						newAirplane.next = tmp;
						newAirplane.prev = currentAir;

					} else {
						currentAir.next = newAirplane;
						newAirplane.prev = currentAir;
						currentAir.dest = newAirplane.dest;
						current.airplain.depart_time = newAirplane.depart_time;

					}
				}
			
			current.airplain = current.airplain.next;
			}
		}

		Airline currenttt = Main.header;
		int i = 1;
		Airplane cc = header.airplain;

		System.out.print("Airline" + i + " = ");

		while (cc != null) {
			System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

			cc = cc.next;
		}
		i = 2;
		currenttt = currenttt.next;
		System.out.println();
		while (currenttt != Main.header) {
			cc = currenttt.airplain;
			if (cc != null) {
				System.out.print("Airline" + i + " = ");

				while (cc != null) {
					System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

					cc = cc.next;
				}
				System.out.println();
			}
			currenttt = currenttt.next;
			i++;

		}

		System.out.println("DONE");

		return true;

	}

	/**
	 * @brief Travel
	 *
	 * @param dest      Destination
	 * @param timestamp Departure time
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean travel(int dest, int timestamp) {
		System.out.print("Destination=");
		Flights tmp2 = null;
		Flights tmp = DESTINATIONS[dest].flight_p;
		while (tmp != DESTINATIONS[dest].sentinel_p) {
			if (timestamp < tmp.depart_time) {
				tmp2 = tmp;
				while (tmp2 != DESTINATIONS[dest].sentinel_p) {
					System.out.print("<" + tmp2.pId + ":" + tmp2.depart_time + ">");
					tmp2 = tmp2.next;
					System.out.print(",");
				}

			}
			tmp = tmp.next;
		}
		System.out.println("\nDONE");
		return true;
	}

	/**
	 * @brief Print airlines
	 *
	 * @return true on success 0 on failure
	 */
	public static boolean print_airlines() {

		Airline currenttt = Main.header;
		int i = 1;
		Airplane cc = header.airplain;

		System.out.println("Airline" + i + "=" + currenttt.aId);
		if (cc != null) {
			System.out.print("    Airplanes" + i + "=");

			while (cc != null) {
				System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

				cc = cc.next;
			}
		}
		i = 2;
		currenttt = currenttt.next;
		while (currenttt != Main.header) {
			cc = currenttt.airplain;
			System.out.println("Airline" + i + "=" + currenttt.aId);
			if (cc != null) {
				System.out.print("    Airplanes" + i + "=");
				while (cc != null) {
					System.out.print("<" + cc.pId + ":" + currenttt.aId + ">,");

					cc = cc.next;
				}
				System.out.println();
			}
			currenttt = currenttt.next;
			i++;

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

		Flights tmp;
		for (int i = 0; i < Main.DESTINATIONS.length; i++) {
			tmp = Main.DESTINATIONS[i].flight_p;
			if (tmp != null) {
				System.out.print("Destination" + i + "=");
				while (tmp != Main.DESTINATIONS[i].sentinel_p) {
					System.out.print("<" + tmp.pId + ":" + tmp.depart_time + ">,");
					tmp = tmp.next;
				}
				System.out.println();
			}

		}
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
//	public static void main(String[] args) {
//		initialize();
//		while (true) {
//			Scanner sc = new Scanner(System.in);
//			char c = sc.next().charAt(0);
//
//			switch (c) {
//			case 'R':
//				int num = sc.nextInt();
//
//				register_airline(num);
//
//				break;
//
//			case 'I':
//
//				int aid = sc.nextInt();
//				int pid = sc.nextInt();
//				int dest = sc.nextInt();
//				int dep = sc.nextInt();
//				insert_airplanes(aid, pid, dest, dep);
//				break;
//			case 'C':
//				aid = sc.nextInt();
//				pid = sc.nextInt();
//				dest = sc.nextInt();
//				cancel_flight(aid, pid, dest);
//				System.out.println("\nDONE");
//				break;
//			case 'D':
//				aid = sc.nextInt();
//				delete_airline(aid);
//				break;
//			case 'A':
//				aid=sc.nextInt();
//				int aid2=sc.nextInt();
//				acquisition_airline(aid, aid2);
//				break;
//			case 'F':
//				dest = sc.nextInt();
//				int ts = sc.nextInt();
////				find_flight(dest, ts);
////				myL.acquisition_airline(aid1, aid2);
////				myA.print_I();
//				System.out.println("DONE");
//				break;
//			case 'S':
//
//				break;
//			case 'P':
//				aid=sc.nextInt();
//				partition_airplanes(aid);
//				break;
//			case 'T':
//				break;
//			case 'X':
//				break;
//			case 'Y':
//				break;
//			default:
//
//			}
//		}
//
//	}
	public static void main(String[] args) throws FileNotFoundException, IOException {
		initialize();

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
			 * Register airline R <aId>
			 */
			case 'R': {
				int aId = Integer.parseInt(params[1]);

				if (register_airline(aId)) {
					System.out.println("R succeeded");
				} else {
					System.err.println("R failed");
				}

				break;
			}

			/*
			 * Insert new airplane I <aId> <pId> <dest> <depart_time>
			 */
			case 'I': {
				int aId = Integer.parseInt(params[1]);
				int pId = Integer.parseInt(params[2]);
				int dest = Integer.parseInt(params[3]);
				int depart_time = Integer.parseInt(params[4]);

				if (insert_airplanes(aId, pId, dest, depart_time)) {
					System.out.println("I succeeded");
				} else {
					System.err.println("I failed");
				}

				break;
			}

			/*
			 * Cancel flight C <aId> <pId> <dest>
			 */
			case 'C': {
				int aId = Integer.parseInt(params[1]);
				int pId = Integer.parseInt(params[2]);
				int dest = Integer.parseInt(params[3]);

				if (cancel_flight(aId, pId, dest)) {
					System.out.println("C succeeded");
				} else {
					System.err.println("C failed");
				}

				break;
			}

			/*
			 * Delete airline D <aId>
			 */
			case 'D': {
				int aId = Integer.parseInt(params[1]);

				if (delete_airline(aId)) {
					System.out.println("D succeeded");
				} else {
					System.err.println("D failed");
				}

				break;
			}

			/*
			 * Acquisition airline A <uid1> <uid2>
			 */
			case 'A': {
				int aId1 = Integer.parseInt(params[1]);
				int aId2 = Integer.parseInt(params[2]);

				if (acquisition_airline(aId1, aId2)) {
					System.out.println("A succeeded");
				} else {
					System.err.println("A failed");
				}

				break;
			}

			/*
			 * Subsidiary airline S <aId1> <aId2> <dest>
			 */
			case 'S': {
				int aId1 = Integer.parseInt(params[1]);
				int aId2 = Integer.parseInt(params[2]);
				int dest = Integer.parseInt(params[3]);

				if (subsidiary_airiline(aId1, aId2, dest)) {
					System.out.println("S succeeded");
				} else {
					System.err.println("S failed");
				}

				break;
			}

			/*
			 * Partition airplanes P <aId>
			 */
			case 'P': {
				int aId = Integer.parseInt(params[1]);

				if (partition_airplanes(aId)) {
					System.out.println("P succeeded");
				} else {
					System.err.println("P failed");
				}

				break;
			}

			/*
			 * Travel T <dest> <timestamp>
			 */
			case 'T': {
				int dest = Integer.parseInt(params[1]);
				int timestamp = Integer.parseInt(params[2]);

				if (travel(dest, timestamp)) {
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
			 * Print destinations flights Y
			 */
			case 'Y': {
				if (print_destinations()) {
					System.out.println("Y succeeded");
				} else {
					System.err.println("Y failed");
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
