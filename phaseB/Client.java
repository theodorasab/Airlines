
public class Client {
	int cid;
	int miles;
	Client lc;
	Client mc;
	Client rc;

	Client(int cid, int miles) {
		this.cid = cid;
		this.miles = miles;
		this.lc = null;
		this.rc = null;
		this.mc = null;
	}
}
