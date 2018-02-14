package fypScheduling;

public class Ranking implements Comparable {
	

	private int facID;
	private int rank;
	private double FI;
	
	public Ranking(int facID, int rank){
		this.facID = facID;
		this.rank = rank;
		this.setFI(0);
	}
	
	public int getFacID() {
		return facID;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public String toString(){
		return facID + "-" + rank;
	}
	
	public String toString2(){
		return facID + ", rank: " + rank + ", FI: "  + FI;
	}

	public int compareTo(Ranking o) {
		// TODO Auto-generated method stub
		if (this.rank > o.getRank()){
			return o.getRank();
		}
		else if(this.rank > o.getRank()){
			return o.getRank();
		}
		return this.rank;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getFI() {
		return FI;
	}

	public void setFI(double FI){
		this.FI = FI;
	}
}
