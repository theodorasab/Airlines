
public class Airplane {
	int pId;
	int dest;
	int depart_time;
	Airplane prev;
	Airplane next;

	Airplane(int pId, int dest,int depart_time) {
		this.pId = pId;
		this.dest = dest;
		this.depart_time = depart_time;
		this.next = null;
		this.prev = null;
	}
	
}
