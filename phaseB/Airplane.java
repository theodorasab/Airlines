
public class Airplane {
	int pId;
	int dest;
	int depart_time;
	Airplane lc;
	Airplane rc;

	Airplane(int pId, int dest, int depart_time) {
		this.pId = pId;
		this.dest = dest;
		this.depart_time = depart_time;
		this.lc = null;
		this.rc = null;
	}
}
