
public class Airline {
	int aId;
	Airplane airplain;
	Airline next;
	Airline prev;

	Airline(int aId) {
		this.aId = aId;
		this.airplain = null;
		this.next = null;
		this.prev = null;
	}

}