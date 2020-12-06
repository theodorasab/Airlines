
public class Flight {
	int pid;
	int depart_time;
	Flight lc;
	Flight rc;
	int thread_status;

	Flight(int pId, int depart_time) {
		this.pid = pId;
		this.depart_time = depart_time;
		this.lc = null;
		this.rc = null;
		this.thread_status=3;
	}
}
