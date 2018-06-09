package fypScheduling;

public class Queue {
	 private int maxSize;

	  private int[] queArray;

	  private int front;

	  private int rear;

	  private int nItems;
	  private double[] worstList;

	  public Queue(int s) {
	    maxSize = s;
	    queArray = new int[maxSize];
	    front = 0;
	    rear = -1;
	    nItems = 0;
	    worstList = new double[maxSize];
	  }

	  public void insert(int j) {
		  
		if(isFull()) {
			remove();
		}
	    if (rear == maxSize - 1) {
		      rear = -1;
	    }
	    queArray[++rear] = j;
	    nItems++;
	  }


	  public int remove() {
	    int temp = queArray[front++];
	    if (front == maxSize)
	      front = 0;
	    nItems--;
	    return temp;
	  }

	  public int peekFront() {
	    return queArray[front];
	  }

	  public boolean isEmpty() {
	    return (nItems == 0);
	  }

	  public boolean isFull() {
	    return (nItems == maxSize);
	  }

	  public int size() {
	    return nItems;
	  }
}
