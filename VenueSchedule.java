package fypScheduling;

public class VenueSchedule {

	public static final int NO_OF_DAYS = 5;
	public static final int NO_OF_SLOT = 9;

	private int[][] timeSlots;
	private Venue room;
	
	
	public VenueSchedule(Venue room){
		this.timeSlots = new int[NO_OF_DAYS][NO_OF_SLOT];
		this.room = room;
	}
	
	public boolean hasEvent(int day, int slot){
		if (timeSlots[day][slot] == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void setEvent(int day, int slot, int id){
		this.timeSlots[day][slot] = id;
	}
	
	public int getEvent(int day, int slot){
		int result = timeSlots[day][slot];
		return result;
	}
	
	public Venue getVenue(){
		return this.room;
	}
	
}
