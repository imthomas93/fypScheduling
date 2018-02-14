package fypScheduling;

public class Timetable implements Comparable<Timetable> {

	
	private int fitness;
	
	private VenueSchedule[] venueSchedule;
	
	public Timetable(int noOfRooms){
		venueSchedule = new VenueSchedule[noOfRooms];
	}
	
	public int getFitness() {
		return fitness;
	}


	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
	
	public VenueSchedule[] getVenueSchedule() {
		return venueSchedule;
	}

	public void setVenueSchedule(int pos, VenueSchedule schedule) {
		this.venueSchedule[pos] = schedule;
	}

	@Override
	public int compareTo(Timetable o) {
		// TODO Auto-generated method stub
		
		int oFitness = o.getFitness();
		if(this.fitness > oFitness){
			return -1;
		}
		else if (this.fitness == oFitness){
			return 0;
		}
		else
			return 1;
	}




}
