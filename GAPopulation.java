package fypScheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

public class GAPopulation {
	
	private LinkedList<Timetable> individuals;
	
	public GAPopulation(){
		individuals = new LinkedList<Timetable>();
	}
	  // time slot class used when creating the random population
	  public class TimeSlot {
	    private int roomId;
	    private int day;
	    private int timeSlot;
	    public TimeSlot(int roomId, int day, int timeSlot) {
	      this.roomId = roomId;
	      this.day = day;
	      this.timeSlot = timeSlot;
	    }
	  }
	
	  public void createRandomIndividuals(int target, Utilities utility){
			Map<Integer, Venue> rooms = utility.getVenues();
			int noOfRooms = utility.getVenues().size();
			for(int i = 0; i < target; i++){
				// register all available timeslots
				ArrayList<TimeSlot> availableSlotsList = new ArrayList<TimeSlot>();
				for (int venueId : rooms.keySet()){
					
					// add all "free" slot
					for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
						for (int slot =0; slot < VenueSchedule.NO_OF_SLOT; slot++){
							TimeSlot child = new TimeSlot(venueId, day, slot);
							availableSlotsList.add(child);
						}
					}
				}

				Timetable timetable = new Timetable(noOfRooms);
				
				for (int venueId : rooms.keySet()){
					Venue venue = rooms.get(venueId);
					VenueSchedule vs = new VenueSchedule(venue);
					timetable.setVenueSchedule(venueId, vs);
				}
				
				
				// assign events to any randomly selected available timeslot
				Random rand = new Random(System.currentTimeMillis());
			
				for(Event child : utility.getEvents().values()){
					
					
					if (child.getEventDuration() == 2) {			
						int randGen = rand.nextInt(availableSlotsList.size()-1);
						int randGen2 = randGen+1;
						
						TimeSlot avail1 = availableSlotsList.get(randGen);
						TimeSlot avail2 = availableSlotsList.get(randGen2);
						
						while(avail1.day != avail2.day && ((avail1.timeSlot+1)) != avail2.timeSlot) {
							// if day are not same, means its not consecutive, regenerate
							randGen = rand.nextInt(availableSlotsList.size()-1);
							randGen2 = randGen+1;
							
							avail1 = availableSlotsList.get(randGen);
							avail2 = availableSlotsList.get(randGen2);
						}
						
						VenueSchedule vs = timetable.getVenueSchedule()[avail1.roomId];
						vs.setEvent(avail1.day, avail1.timeSlot, child.getId());
						VenueSchedule vs2 = timetable.getVenueSchedule()[avail2.roomId];
						vs2.setEvent(avail2.day, avail2.timeSlot, child.getId());
					}
					else {
						TimeSlot availableSlot = availableSlotsList.get(rand.nextInt(availableSlotsList.size()));
						VenueSchedule vs = timetable.getVenueSchedule()[availableSlot.roomId];
						vs.setEvent(availableSlot.day, availableSlot.timeSlot, child.getId());
						availableSlotsList.remove(availableSlot);
					}
				}
				individuals.add(timetable);
				availableSlotsList.clear();
			}
		}
	  public void createRandomIndividuals1(int target, Utilities utility){
			Map<Integer, Venue> rooms = utility.getVenues();
			int noOfRooms = utility.getVenues().size();
			for(int i = 0; i < target; i++){
				// register all available timeslots
				ArrayList<TimeSlot> availableSlotsList = new ArrayList<TimeSlot>();
				for (int venueId : rooms.keySet()){
					
					// add all "free" slot
					for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
						for (int slot =0; slot < VenueSchedule.NO_OF_SLOT; slot++){
							TimeSlot child = new TimeSlot(venueId, day, slot);
							availableSlotsList.add(child);
						}
					}
				}

				Timetable timetable = new Timetable(noOfRooms);
				
				for (int venueId : rooms.keySet()){
					Venue venue = rooms.get(venueId);
					VenueSchedule vs = new VenueSchedule(venue);
					timetable.setVenueSchedule(venueId, vs);
				}
				
				
				// assign events to any randomly selected available timeslot
				Random rand = new Random(System.currentTimeMillis());
				
				for(Event child : utility.getEvents().values()){
					TimeSlot availableSlot = availableSlotsList.get(rand.nextInt(availableSlotsList.size()));
					VenueSchedule vs = timetable.getVenueSchedule()[availableSlot.roomId];
					vs.setEvent(availableSlot.day, availableSlot.timeSlot, child.getId());
					availableSlotsList.remove(availableSlot);
				}
				individuals.add(timetable);
				availableSlotsList.clear();
			}
		}
	
	public Timetable getTopIndividual(){
		return individuals.getFirst();
	}
	
	public Timetable getWorstIndividual(){
		return individuals.getLast();
	}
	
	public Timetable getIndividual(int target){
		return this.individuals.get(target);
	}
	
	public void addIndividual(Timetable target){
		individuals.add(target);
	}
	
	public void addIndividualSorted(Timetable target){
		ListIterator<Timetable> it = individuals.listIterator();
		ListIterator<Timetable> it2 = individuals.listIterator();
		
		while(it.hasNext()){
			if (it.next().getFitness() < target.getFitness()){
				it2.add(target);
				break;
			}
			
			it2.next();
		}
	}
	
	public ListIterator<Timetable> listIterator(){
		return individuals.listIterator();
	}
	
	public void sortIndividuals(){
		Collections.sort(individuals);
	}
	
	public int size(){
		return individuals.size();
	}
	
	/*
		public void createRandomIndividuals(int target, Utilities utility){
		Map<Integer, Venue> rooms = utility.getVenues();
		int noOfRooms = utility.getVenues().size();
		for(int i = 0; i < target; i++){
			// register all available timeslots
			ArrayList<TimeSlot> availableSlotsList = new ArrayList<TimeSlot>();
			for (int venueId : rooms.keySet()){
				
				// add all "free" slot
				for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
					for (int slot =0; slot < VenueSchedule.NO_OF_SLOT; slot++){
						TimeSlot child = new TimeSlot(venueId, day, slot);
						availableSlotsList.add(child);
					}
				}
			}

			Timetable timetable = new Timetable(noOfRooms);
			
			for (int venueId : rooms.keySet()){
				Venue venue = rooms.get(venueId);
				VenueSchedule vs = new VenueSchedule(venue);
				timetable.setVenueSchedule(venueId, vs);
			}
			
			
			// assign events to any randomly selected available timeslot
			Random rand = new Random(System.currentTimeMillis());
		
			for(Event child : utility.getEvents().values()){
				
				
				if (child.getEventDuration() == 2) {			
					int randGen = rand.nextInt(availableSlotsList.size()-1);
					int randGen2 = randGen+1;
					
					TimeSlot avail1 = availableSlotsList.get(randGen);
					TimeSlot avail2 = availableSlotsList.get(randGen2);
					
					while(avail1.day != avail2.day && ((avail1.timeSlot+1)) != avail2.timeSlot) {
						// if day are not same, means its not consecutive, regenerate
						randGen = rand.nextInt(availableSlotsList.size()-1);
						randGen2 = randGen+1;
						
						avail1 = availableSlotsList.get(randGen);
						avail2 = availableSlotsList.get(randGen2);
					}
					
					VenueSchedule vs = timetable.getVenueSchedule()[avail1.roomId];
					vs.setEvent(avail1.day, avail1.timeSlot, child.getId());
					VenueSchedule vs2 = timetable.getVenueSchedule()[avail2.roomId];
					vs2.setEvent(avail2.day, avail2.timeSlot, child.getId());
				}
				else {
					TimeSlot availableSlot = availableSlotsList.get(rand.nextInt(availableSlotsList.size()));
					VenueSchedule vs = timetable.getVenueSchedule()[availableSlot.roomId];
					vs.setEvent(availableSlot.day, availableSlot.timeSlot, child.getId());
					availableSlotsList.remove(availableSlot);
				}
			}
			individuals.add(timetable);
			availableSlotsList.clear();
		}
	}

	 */
}
