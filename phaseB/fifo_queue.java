
public class fifo_queue {

	Client Q; /* dynamically allocated array to store queue elements */
	/* used for level-order traversals of priority queue */
	int front;
	int length;

	public fifo_queue(int front, int length) {
		this.front = front;
		this.length = length;
	}

}
