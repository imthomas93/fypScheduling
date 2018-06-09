package fypScheduling;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import com.mysql.fabric.xmlrpc.base.Array;

import fyp.CourseHistory;
import fypScheduling.Event.Type;

public class GeneticAlgorithm {
	private int TARGET_FITNESS = 0;
	private int MAX_POPULATION_SIZE;
	private int MUTATION_PROB;
	private int CROSSOVER_PROB;
	private int SELECTION_TYPE;
    HashMap<String, Course> courseNumList = new HashMap<String, Course>();

	private GAPopulation population;
	private Utilities utility;
	private 	int numGeneration = 1;


	public GeneticAlgorithm(){
		utility = new Utilities();
	}
	
	/*
	 * SETUP
	 */	
	public void loadDatabase(StimulatedAnnealing saAlgo) {
		// TODO Auto-generated method stub
		utility.getCourses().clear();
		utility.getVenues().clear();
		utility.getFaculty().clear();
		utility.getGrouping().clear();
		utility.getEvents().clear();

		utility.getCourses().putAll(saAlgo.getUtilites().getCourses());
		utility.getVenues().putAll(saAlgo.getUtilites().getVenues());
		utility.getFaculty().putAll(saAlgo.getUtilites().getFaculty());
		utility.getGrouping().putAll(saAlgo.getUtilites().getGrouping());
		utility.getEvents().putAll(saAlgo.getUtilites().getEvents());
	}
	

	/*
	 * ALGORITHM
	 */
	public Timetable generateTimetable(){

		// create initial random population
		createRandomPopulation();
		 
		// evalulate timetable fitness and sort base on fitness
		ListIterator<Timetable> it = population.listIterator();
		while(it.hasNext()){
			Timetable child = it.next();
			fitness(child);
		}
		population.sortIndividuals();
		
		
		// loop while fitness is not at desired level
		while((population.getTopIndividual().getFitness() < TARGET_FITNESS)){
			// produce new children
			GAPopulation child = breed(population, MAX_POPULATION_SIZE);
			population = selection(population, child);
			//population = selectionRoulette(population, child);
			//population = selectionTournament(population, child);

			
			// sort population by fitness
			population.sortIndividuals();
			if (numGeneration > 50) {
				mutation2(population.getTopIndividual());
				repairTimetable(population.getTopIndividual());
				fitness(population.getTopIndividual());
			}
			
			numGeneration++;	
			System.out.println("Generation: " + numGeneration + "\tBest Fitness: " + population.getTopIndividual().getFitness() +"\tWorst Fitness: " + population.getWorstIndividual().getFitness());
			String str = "Generation: " + numGeneration + "\tBest Fitness: " + population.getTopIndividual().getFitness() +"\tWorst Fitness: " + population.getWorstIndividual().getFitness() + "\n";
			GUI.textbox.append(str);
			evaluateBestFitness(population.getTopIndividual());
		}
		
		utility.setBestTimetable(population.getTopIndividual());
		
		return population.getTopIndividual();
	}


	/*
	 * Genetic Algorithm
	 */
	public GAPopulation createRandomPopulation(){
		population = new GAPopulation();
		population.createRandomIndividuals(MAX_POPULATION_SIZE, utility);
		return population;
	}
	
	private Timetable next(ListIterator<Timetable> target) {
	    return target.hasNext() ? target.next() : null;
	  }
	
	private GAPopulation breed(GAPopulation target, int targetNo){
		GAPopulation result = new GAPopulation();
		
		// calculate fitness of individual
		int[] pseudoFitness = new int[target.size()];
		int smallestFitness = target.getWorstIndividual().getFitness();
		
	    smallestFitness = smallestFitness >= 0 ? 0 : smallestFitness;
	    int i =0, fitnessSum=0; 
	    ListIterator<Timetable> it = target.listIterator();
	    while(it.hasNext()){
	    	// smallest possible =1
	        pseudoFitness[i] = it.next().getFitness() + -1 * smallestFitness + 1;
	        fitnessSum += pseudoFitness[i];
	        i++;	   
	    }
	    
	    // create an alias index
	    int[] alias = new int[fitnessSum+1];
	    
	    // add individual index a proportionate amount of times
	   int aliasIndex = 0;
	   it = target.listIterator();
	   for (int individual = 0; individual < (population.size()); individual++) {
	      for (int j = 0; j < pseudoFitness[individual]; j++) {
	        alias[aliasIndex] = individual;
	        aliasIndex++;
	      }
	   }
		    
		Random rand = new Random(System.currentTimeMillis());
		
		while(result.size() < targetNo){
			if (alias.length == 0) {
		        break;
		      }

			int pi1 = alias[rand.nextInt(alias.length)];
			int numPi1 = pseudoFitness[pi1];
			int aIndex = rand.nextInt(alias.length-numPi1);
			
			int ai = 0, j =0;
			
			for(j = 0 ; j<alias.length && ai <aIndex; j++){
				// skip if not at span of first parent's index
				while(j < (alias.length-1) && alias[j] == pi1){
					j++;
				}
				ai++;
			}
			
			int pi2 = alias[j];
			Timetable target1 = target.getIndividual(pi1);
			Timetable target2 = target.getIndividual(pi2);
			
			Timetable child = crossOverWithPoint(target1, target2);
			
			// mutate the child
			mutation(child);
			if (numGeneration > 50) {
				mutation2(child);
			}
			repairTimetable(child);
			fitness(child);
			
			result.addIndividual(child);

		}
		return result;
	}
	
	/*
	 * SELECTION ALGO - merge the best parents and children population into 1 single population
	 */
	private GAPopulation selection(GAPopulation population, GAPopulation children){
	
		GAPopulation result = new GAPopulation();
		children.sortIndividuals();
		
		ListIterator<Timetable> targetParents = population.listIterator();
		ListIterator<Timetable> targetChildren = children.listIterator();
		
		Timetable nextParent = next(targetParents);
		Timetable nextChild = next(targetChildren);
		
		while(result.size() < MAX_POPULATION_SIZE){
			if (nextChild != null){
				if (nextChild.getFitness() > nextParent.getFitness()){
					result.addIndividual(nextChild);
					
					nextChild = next(targetChildren);
				}
				else{
					result.addIndividual(nextParent);
					nextParent = next(targetParents);
				}
			}
			else{
				if(nextParent!=null){
					result.addIndividual(nextParent);
					nextParent = next(targetParents);
				}
			}
		}
		return result;
	}
	
	private GAPopulation selectionRoulette(GAPopulation population, GAPopulation children){
		GAPopulation result = new GAPopulation();
		children.sortIndividuals();
		
		ListIterator<Timetable> targetParents = population.listIterator();
		ListIterator<Timetable> targetChildren = children.listIterator();
		
		Timetable nextParent = next(targetParents);
		Timetable nextChild = next(targetChildren);
		
		while(result.size() < MAX_POPULATION_SIZE){
			if (nextChild != null){
				if (nextChild.getFitness() > nextParent.getFitness()){
					double prob = randomDouble();
					if (prob > 0.3) {
						result.addIndividual(nextChild);
						nextChild = next(targetChildren);
					}
					else {
						result.addIndividual(nextChild);
						nextChild = next(targetChildren);
					}
				}
				else{
					double prob = randomDouble();
					if (prob > 0.7) {
						result.addIndividual(nextChild);
						nextChild = next(targetChildren);
					}else {
						result.addIndividual(nextParent);
						nextParent = next(targetParents);
					}
				}
			}
			else{
				if(nextParent!=null){
					result.addIndividual(nextParent);
					nextParent = next(targetParents);
				}
			}
		}
		return result;
	}

	private GAPopulation selectionTournament(GAPopulation population, GAPopulation children){
		GAPopulation result = new GAPopulation();

		ListIterator<Timetable> targetParents = population.listIterator();
		ListIterator<Timetable> targetChildren = children.listIterator();
		
		int avg = (population.size() + children.size()) / 4;
		for (int i = 0; i < avg; i++) {
			double rand = randomDouble();
			if (rand > 0.5) {
			}
		}
		return result;
	}

	private Timetable crossOverWithPoint(Timetable target1, Timetable target2){
		
		Timetable result = new Timetable(utility.getNoOfVenues());

		int interval = utility.getNoOfVenues() * VenueSchedule.NO_OF_SLOT * VenueSchedule.NO_OF_DAYS;
		int gene = 0;
		int point = new Random(System.currentTimeMillis()).nextInt(interval);
		
		VenueSchedule[] vsList1 = target1.getVenueSchedule();
		VenueSchedule[] vsList2 = target2.getVenueSchedule();
		
		for (int i = 0; i <utility.getNoOfVenues(); i++){
			VenueSchedule vsChild = new VenueSchedule(vsList1[i].getVenue());
			
			for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
				for (int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
					int temp;
					
					if (gene<point){
						temp = vsList1[i].getEvent(day, slot);
					}
					else{
						temp = vsList2[i].getEvent(day, slot);
					}
					
					vsChild.setEvent(day, slot, temp);
					gene++;
				}
			}
			result.setVenueSchedule(i, vsChild);
		}	
		return result;
	}
	
	/*
	 * REPAIR ALGO
	 */
	
	private void repairTimetable1(Timetable target){
		VenueSchedule[] vsList = target.getVenueSchedule();
		HashMap<Integer, LinkedList<VenueDayTime>> locations = new HashMap<Integer, LinkedList<VenueDayTime>>();
		LinkedList<VenueDayTime> unusedSlots = new LinkedList<VenueDayTime>();
		
		for(int eventID : utility.getEvents().keySet()){
			locations.put(eventID, new LinkedList<VenueDayTime>());
		}
		for(int i = 0; i < utility.getNoOfVenues(); i++){
			VenueSchedule vsChild = vsList[i];
			for (int day =0; day < VenueSchedule.NO_OF_DAYS; day++){
				for(int slot= 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
					int bookedEvent = vsChild.getEvent(day, slot);
					if (bookedEvent == 0){
						unusedSlots.add(new VenueDayTime(i, day, slot));
					}
					else{
						locations.get(bookedEvent).add(new VenueDayTime(i, day, slot));
					}
				}
			}
		}
	    List<Integer> unbookedEvents = new LinkedList<Integer>();
	    
	    for(int eventID : utility.getEvents().keySet()){
	    	if (locations.get(eventID).size() == 0){
	    		//event is not booked
	    		unbookedEvents.add(eventID);
	    	}
	    	else if(locations.get(eventID).size() > 1){
	    		// event is booked more than once
	    		LinkedList<VenueDayTime> slots = locations.get(eventID);
	    		Collections.shuffle(slots);
	    		
	    		while(slots.size() >1){
	    			VenueDayTime child = slots.removeFirst();
	    			
	    			// mark as unusued
	    			unusedSlots.add(child);
	    			vsList[child.venue].setEvent(child.day, child.time, 0);
	    		}
	    	}
	    }
	    
	    
	    Collections.shuffle(unusedSlots);
	    for (int eventID : unbookedEvents){
	    	// place unbooked event in an unused slot
	    	VenueDayTime child = unusedSlots.removeFirst();
			vsList[child.venue].setEvent(child.day, child.time, eventID);
	    }
	}
	
	private void repairTimetable(Timetable target){
		VenueSchedule[] vsList = target.getVenueSchedule();
		HashMap<Integer, LinkedList<VenueDayTime>> locations = new HashMap<Integer, LinkedList<VenueDayTime>>();
		LinkedList<VenueDayTime> unusedSlots = new LinkedList<VenueDayTime>();
		
		for(int eventID : utility.getEvents().keySet()){
			locations.put(eventID, new LinkedList<VenueDayTime>());
		}
		
		for(int i = 0; i < utility.getNoOfVenues(); i++){
			VenueSchedule vsChild = vsList[i];
			for (int day =0; day < VenueSchedule.NO_OF_DAYS; day++){
				for(int slot= 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
					int bookedEvent = vsChild.getEvent(day, slot);
					if (bookedEvent == 0){
						unusedSlots.add(new VenueDayTime(i, day, slot));
					}
					else{
						if (utility.getEvents().get(bookedEvent).getEventDuration() == 2) {
							if (slot == 0) {
								int bookedEventAfter = vsChild.getEvent(day, (slot+1));
								
								if (bookedEventAfter == bookedEvent) {
									locations.get(bookedEvent).add(new VenueDayTime(i, day, slot));
								}
								else {
									unusedSlots.add(new VenueDayTime(i, day, slot));
									// think of sol to remove 1hr id with 2hr allocation
									vsList[i].setEvent(day, slot, 0);
								}
							}
							else if (slot == (VenueSchedule.NO_OF_SLOT-1)) {
								int bookedEventBefore = vsChild.getEvent(day, (slot-1));
								if (bookedEventBefore == bookedEvent) {
									locations.get(bookedEvent).add(new VenueDayTime(i, day, slot));
								}
								else {
									unusedSlots.add(new VenueDayTime(i, day, slot));									
									// think of sol to remove 1hr id with 2hr allocation
									vsList[i].setEvent(day, slot, 0);
								}
							}
							else {
								int bookedEventAfter = vsChild.getEvent(day, (slot+1));
								int bookedEventBefore = vsChild.getEvent(day, (slot-1));
								
								if ((bookedEventBefore == bookedEvent) || (bookedEventAfter == bookedEvent)){
									locations.get(bookedEvent).add(new VenueDayTime(i, day, slot));
								}
								else {
									unusedSlots.add(new VenueDayTime(i, day, slot));
									// think of sol to remove 1hr id with 2hr allocation
									vsList[i].setEvent(day, slot, 0);
								}
							}
						}else {
							locations.get(bookedEvent).add(new VenueDayTime(i, day, slot));
						}
					}
				}
			}
		}
	    List<Integer> unbookedEvents = new LinkedList<Integer>();
	    
	    for(int eventID : utility.getEvents().keySet()){
		    	if (locations.get(eventID).size() == 0){
		    		//event is not booked
		    		unbookedEvents.add(eventID);
		    	}
		    	else if(locations.get(eventID).size() > 1 && utility.getEvents().get(eventID).getEventDuration() == 1){
	    			// event is booked more than once
		    		LinkedList<VenueDayTime> slots = locations.get(eventID);
		    		Collections.shuffle(slots);
		    		
		    		while(slots.size() >1){
		    			VenueDayTime child = slots.removeFirst();
		    			
		    			// mark as unusued
		    			unusedSlots.add(child);
		    			vsList[child.venue].setEvent(child.day, child.time, 0);
		    		}
		    	}
		    	else if(locations.get(eventID).size() > 2 && utility.getEvents().get(eventID).getEventDuration() == 2){
	    			// event is booked more than once
		    		LinkedList<VenueDayTime> slots = locations.get(eventID);
		    		
		    		while(slots.size() >2){
		    			VenueDayTime child = slots.removeFirst();
		    			
		    			// mark as unusued
		    			unusedSlots.add(child);
		    			vsList[child.venue].setEvent(child.day, child.time, 0);
		    		}
		    	}
	    }
	    
	    
	    Collections.shuffle(unusedSlots);
	    for (int eventID : unbookedEvents){
	    	// place unbooked event in an unused slot
	    	
	    		if (utility.getEvents().get(eventID).getEventDuration() == 1) {
	    			boolean flag = true;
	    			while(flag) {
	    				VenueDayTime child = unusedSlots.removeFirst();
	    				if (utility.getVenues().get(child.venue).getType() == utility.getEvents().get(eventID).getType()) {
	    					vsList[child.venue].setEvent(child.day, child.time, eventID);
			    			flag = false;
	    				}else {
	    					// put back the unused slot and reshuffle
			    			unusedSlots.add(child);
			    		    Collections.shuffle(unusedSlots);
	    				}
	    			}
	    		}
	    		else {
	    			boolean flag = true;
	    			while(flag) {
	    				int secondSlot;
		    			VenueDayTime child = unusedSlots.removeFirst();
		    			int spec = utility.getEvents().get(eventID).getGrouping().getSpecialisation();
		    			if (spec == 1) {
		    				if (utility.getVenues().get(child.venue).getType() == Event.Type.LEC) {
		    					if (child.time == (VenueSchedule.NO_OF_SLOT-1)) {
				    				secondSlot = child.time-1;
				    			}
				    			else{
				    				secondSlot = child.time+1;
				    			}
				    			
				    			int secondEvent = vsList[child.venue].getEvent(child.day, secondSlot);
				    			if (secondEvent == 0) {
				    				// second slot is free to add
					    			int index = 0, pos = -1;
					    			for (VenueDayTime second : unusedSlots) {
					    				if ((second.day == child.day) && (second.time == secondSlot) && (second.venue == child.venue)) {
					    					pos = index;
					    					break;
					    				}else {
						    				index++;
					    				}
					    			}
					    			
					    			if (pos != -1) {
					    				unusedSlots.remove(pos);
						    			vsList[child.venue].setEvent(child.day, secondSlot, eventID);
						    			vsList[child.venue].setEvent(child.day, child.time, eventID);
						    			flag = false;
					    			}else {
					    				// put back the unused slot and reshuffle
						    			unusedSlots.add(child);
						    		    Collections.shuffle(unusedSlots);
					    			}
				    			}
				    			else {
				    				// put back the unused slot and reshuffle
					    			unusedSlots.add(child);
					    		    Collections.shuffle(unusedSlots);
				    			}
				    			
		    				}
		    				else {
		    					// put back the unused slot and reshuffle
				    			unusedSlots.add(child);
				    		    Collections.shuffle(unusedSlots);
			    			}
		    			}else {
		    				if (utility.getVenues().get(child.venue).getType() == utility.getEvents().get(eventID).getType()) {
		    					if (child.time == (VenueSchedule.NO_OF_SLOT-1)) {
				    				secondSlot = child.time-1;
				    			}
				    			else{
				    				secondSlot = child.time+1;
				    			}
				    			
				    			int secondEvent = vsList[child.venue].getEvent(child.day, secondSlot);
				    			if (secondEvent == 0) {
				    				// second slot is free to add
					    			int index = 0, pos = -1;
					    			for (VenueDayTime second : unusedSlots) {
					    				if ((second.day == child.day) && (second.time == secondSlot) && (second.venue == child.venue)) {
					    					pos = index;
					    					break;
					    				}else {
						    				index++;
					    				}
					    			}
					    			
					    			if (pos != -1) {
					    				unusedSlots.remove(pos);
						    			vsList[child.venue].setEvent(child.day, secondSlot, eventID);
						    			vsList[child.venue].setEvent(child.day, child.time, eventID);
						    			flag = false;
					    			}else {
					    				// put back the unused slot and reshuffle
						    			unusedSlots.add(child);
						    		    Collections.shuffle(unusedSlots);
					    			}
				    			}
				    			else {
				    				// put back the unused slot and reshuffle
					    			unusedSlots.add(child);
					    		    Collections.shuffle(unusedSlots);
				    			}
		    				}
		    				else {
		    					// put back the unused slot and reshuffle
				    			unusedSlots.add(child);
				    		    Collections.shuffle(unusedSlots);
		    				}
		    			}
	
	    			}
	    			

	    		}
	    		
	    }
	}	
	/*
	 * MUTATION ALGO - swap timeslot and a day
	 */
	
	private void mutation(Timetable target){
		Random rand = new Random(System.currentTimeMillis());
		VenueSchedule[] vsList = target.getVenueSchedule();
		
		for(int i = 0; i < utility.getNoOfVenues(); i++){
			//TODO: NEED TO THink how to mutate
			VenueSchedule vs = vsList[i];
			
			// loop all day
			for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
				// loop all slot
				for (int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
					
					if (rand.nextInt(1000) < MUTATION_PROB){
						// random > gene, therefore, mutate
						int swapTargetDay = rand.nextInt(VenueSchedule.NO_OF_DAYS);
						int swapTargetSlot = rand.nextInt(VenueSchedule.NO_OF_SLOT);
						
						int eventId = vs.getEvent(swapTargetDay, swapTargetSlot);
						int swapSrcEventId = vs.getEvent(day, slot);
						vs.setEvent(swapTargetDay, swapTargetSlot, swapSrcEventId);
						vs.setEvent(day,slot, eventId);
					}
				}
			}
		}
	}

	private void mutation2(Timetable target){
		Random rand = new Random(System.currentTimeMillis());
		VenueSchedule[] vsList = target.getVenueSchedule();
		
		for(int i = 0; i < utility.getNoOfVenues(); i++){
			VenueSchedule vs = vsList[i];
			Event.Type venueType = vs.getVenue().getType();
			int venueSize = vs.getVenue().getCapacity();

			// loop all day
			for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
				// loop all slot
				for (int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
					int eventID = vs.getEvent(day, slot);
					if(eventID !=0){
						Event.Type type = utility.getEvents(eventID).getType();
						int spec = utility.getEvents(eventID).getGrouping().getSpecialisation();
						if (spec == 1) {
							if (venueType != Event.Type.LEC) {
								vs.setEvent(day,slot, 0);
							}
						}
						else if(type != venueType){
							vs.setEvent(day,slot, 0);
						}
						
						int eventSize = utility.getEvents(eventID).getSize();
						if (venueSize < eventSize){
							vs.setEvent(day,slot, 0);
						}
						
						
						for (Faculty facChild : utility.getFaculty().values()){
							facChild.resetDayOfWork();
							Event eventChild = utility.getEvents(eventID);
							if(eventChild.getType() != Event.Type.LAB) {
								ArrayList<Faculty> facList = eventChild.getFaculty();
								for(Faculty fac : facList) {
									if (fac.getId() == facChild.getId()) {
										facChild.increaseDayOfWork(day);
									}
									
									if (day > 3 && facChild.isDayExceeded()) {
										if (facChild.isDayExceeded(day))
										vs.setEvent(day,slot, 0);
									}
								}
							}
						}
						for(Grouping grouping : utility.getGrouping().values()) {
							grouping.resetDayOfWork();
							Event eventChild = utility.getEvents(eventID);
							if(eventChild.getType() == Event.Type.LEC) {
								if (eventChild.getGrouping().getId() == grouping.getId()) {
									grouping.increaseDayOfWork(day);
								}
								if (day > 3 && grouping.isDayExceeded()) {
									if (grouping.isDayExceeded(day))
										vs.setEvent(day,slot, 0);
								}
							}
						}
						
					}
					
					
				}
			}
		}
		
		for (Faculty facChild : utility.getFaculty().values()){
			for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){	
				for (int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
					
					int subResult = 0;
					for (VenueSchedule vsChild : vsList){
						int eventId = vsChild.getEvent(day, slot);
						
						if (eventId != 0){
							Event event = utility.getEvents(eventId);		
							ArrayList<Faculty> facList = event.getFaculty();
							for(Faculty fac : facList) {
								if (fac.getId() == facChild.getId()) {
									subResult++;

								}
								if (subResult > 1) {
									vsChild.setEvent(day,slot, 0);
								}
							}
						}
					}
	
				}
			}
		}
	}

	/*
	 * Fitness constraints solver
	 */
	private void fitness(Timetable target){		
		int groupingBreach = groupingBreach(target);
		int facultyDoubleAllocate = facultyDoubleAllocate(target);
		int venueCapExceed = venueCapExceed(target);
		int venueTypeBreached = venueTypeBreaches(target);
		int facDayWork = dayFacWork(target);
		int groupDayWork = dayGroupWork(target);
	
		//venueTypeBreached = 0;
		
		int breaches = 2*groupingBreach + facultyDoubleAllocate + 4*venueCapExceed + 4*venueTypeBreached + facDayWork + groupDayWork;
		int fitness = breaches *-1;
		target.setFitness(fitness);
	}
	
	private void evaluateBestFitness(Timetable target) {
		// TODO Auto-generated method stub
		int groupingBreach = groupingBreach(target);
		int facultyDoubleAllocate = facultyDoubleAllocate(target);
		int venueCapExceed = venueCapExceed(target);
		int venueTypeBreached = venueTypeBreaches(target);
		int facDayWork = dayFacWork(target);
		int groupDayWork = dayGroupWork(target);


		System.out.println("GroupBreach: " + groupingBreach + "  FacDouble: " + facultyDoubleAllocate + "  venueCap: " + venueCapExceed + "  VenueTypeBreach: " + venueTypeBreached + "\t" + "facDayWK:" + facDayWork
				 + "\t" + "GroupDayWK:" + groupDayWork+ "\n");
	}
	
	private int facultyDoubleAllocate(Timetable target) {
		int result = 0;
		
		VenueSchedule[] vsList = target.getVenueSchedule();
		
		for (Faculty facChild : utility.getFaculty().values()){
			for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){	
				for (int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
					
					int subResult = 0;
					for (VenueSchedule vsChild : vsList){
						int eventId = vsChild.getEvent(day, slot);
						
						if (eventId != 0){
							Event event = utility.getEvents(eventId);		
							ArrayList<Faculty> facList = event.getFaculty();
							for(Faculty fac : facList) {
								if (fac.getId() == facChild.getId()) {
									subResult++;

								}
							}
						}
					}
					
					if (subResult > 1) {
						result += subResult - 1;
					}
				}
			}
		}
		return result;
	}

	private int dayFacWork(Timetable target) {
		int result = 0;
		VenueSchedule[] vsList = target.getVenueSchedule();
		
		for (Faculty facChild : utility.getFaculty().values()){
			facChild.resetDayOfWork();
			for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
				for (int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){	
					
					for(VenueSchedule vs :vsList){
						int eventID = vs.getEvent(day, slot);
						if (eventID != 0) {
							Event eventChild = utility.getEvents(eventID);
							if(eventChild.getType() != Event.Type.LAB) {
								ArrayList<Faculty> facList = eventChild.getFaculty();
								for(Faculty fac : facList) {
									if (fac.getId() == facChild.getId()) {
										facChild.increaseDayOfWork(day);
									}
								}
							}
						}
					}
					
				}	
			}

			if (facChild.countDaysOfWork() > 4) {
				result++;
			}
		}
		return result;
	}
	
	private int dayGroupWork(Timetable target) {
		int result = 0;
		VenueSchedule[] vsList = target.getVenueSchedule();
		
		for(Grouping grouping : utility.getGrouping().values()) {
			grouping.resetDayOfWork();
			for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
				for (int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
					for(VenueSchedule vs :vsList){
						int eventID = vs.getEvent(day, slot);
						if (eventID != 0) {
							Event eventChild = utility.getEvents(eventID);
							if(eventChild.getType() == Event.Type.LEC) {
								if (eventChild.getGrouping().getId() == grouping.getId()) {
									grouping.increaseDayOfWork(day);
								}
							}
						}
					}
				}
			}
			if (grouping.countDaysOfWork() > 4) {
				result++;
			}
		}

		
		return result;
	}
		
	private int groupingBreach(Timetable target) {
		// TODO Auto-generated method stub
		int result = 0;
		VenueSchedule[] vsList = target.getVenueSchedule();	
		
		// loop all day
		for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
			// loop all slot
			for (int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
				for(Grouping grouping : utility.getGrouping().values()){
					
					HashMap<Integer, Integer> eventGroupCount = new HashMap<Integer, Integer>();
					
					for(VenueSchedule vs :vsList){
						int eventID = vs.getEvent(day, slot);
						if(eventID !=0){
							Event event = utility.getEvents(eventID);
							int groupID = event.getGrouping().getId();
							
							if(groupID == grouping.getId()){
								// if booking is for current grouping
								if(!eventGroupCount.containsKey(groupID)){
									eventGroupCount.put(groupID, 1);
								}
								else{
									int oldCount = eventGroupCount.get(groupID);
									eventGroupCount.put(groupID, oldCount+1);
								}
							}
						}
					}
					//find largest group
					int largestGroup, largestSize =0, sumGroupSize =0;;
					for(Map.Entry<Integer, Integer> child: eventGroupCount.entrySet()){
						sumGroupSize += child.getValue();
						
						if(child.getValue() > largestSize){
							largestGroup = child.getKey();
							largestSize = child.getValue();
						}
					}
					result += sumGroupSize - largestSize;
				}
			}
		}
		return result;
	}

	private int venueTypeBreaches(Timetable target) {
		// TODO Auto-generated method stub
		int result = 0;
		VenueSchedule[] vsList = target.getVenueSchedule();	
		
		for(VenueSchedule vs :vsList){
			Event.Type venueType = vs.getVenue().getType();
			
			// loop all day
			for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
				// loop all slot
				for (int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
				
					int eventID = vs.getEvent(day, slot);
					if(eventID !=0){
						Event.Type type = utility.getEvents(eventID).getType();
						int spec = utility.getEvents(eventID).getGrouping().getSpecialisation();
						if (spec == 1) {
							if (venueType != Event.Type.LEC) {
								result++;
							}
						}
						else if(type != venueType){
							result++;
						}
					}
				}
			}

		}
		
		return result;	
	}

	private int venueCapExceed(Timetable target) {
		// TODO Auto-generated method stub
		int result = 0;
		VenueSchedule[] vsList = target.getVenueSchedule();
		
		for (VenueSchedule vsChild : vsList){
			int venueSize = vsChild.getVenue().getCapacity();
			
			// loop all day
			for (int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
				// loop all slot
				for (int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
					int eventID = vsChild.getEvent(day, slot);
					if (eventID !=0){
						int eventSize = utility.getEvents(eventID).getSize();
						if (venueSize < eventSize){
							result++;
						}
					}
				}
			}
		}
		return result;
	}

	/*
	 * Valid on SQ976/ SQ978/ SQ982 only
Valid inbound flights Valid on SQ973/ SQ975/ SQ981 only
	 */
	
	/*
	 * GETTERS & SETTERS
	 */
	public int getTARGET_FITNESS() {
		return TARGET_FITNESS;
	}

	public void setTARGET_FITNESS(int tARGET_FITNESS) {
		TARGET_FITNESS = tARGET_FITNESS;
	}

	public int getMutationProb() {
		return MUTATION_PROB;
	}

	public void setMutationProb(int MUTATION_PROB) {
		this.MUTATION_PROB = MUTATION_PROB;
	}

	public int getCrossoverProb() {
		return CROSSOVER_PROB;
	}

	public void setCrossoverProb(int CROSSOVER_PROB) {
		this.CROSSOVER_PROB = CROSSOVER_PROB;
	}

	public int getSelectionType() {
		return SELECTION_TYPE;
	}

	public void setSelectionType(int SELECTION_TYPE) {
		this.SELECTION_TYPE = SELECTION_TYPE;
	}

	public int getPopulationSize(){
		return MAX_POPULATION_SIZE;
	}

	public void setPopulationSize(int parseInt) {
		this.MAX_POPULATION_SIZE = parseInt;
	}
	
	private double randomDouble(){
		Random r = new Random();
		return r.nextInt(1000) / 1000;
	}

	public void printTimetable(Timetable bestTimetable) {
		int nrSlots =0, nrEvents = 0, startTime = 830;
		ArrayList<Integer> eventList = new ArrayList<Integer>();
		for(VenueSchedule vs : bestTimetable.getVenueSchedule()){
			System.out.println("---------------------------------------------------------------------------------------------");
			System.out.println("Venue: " + vs.getVenue().getRoomName() + "\t Capacity: "+ vs.getVenue().getCapacity());

			for(int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
				System.out.print(startTime +"\t\t");
				startTime+=100;
				for(int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
					int eventID = vs.getEvent(day, slot);
					if(eventID > nrEvents){
						nrEvents = eventID;
					}
					nrSlots++;
					if (eventID == 0){
						System.out.print("[\t" + eventID + "\t\t]");
					}
					else{
						Event child = utility.getEvents(eventID);
						eventList.add(eventID);
						String output = "[ " + child.getCourse().getCourseNum() + " " + child.getFaculty().get(0).getName()+ " ]";	
						output = "[ " + eventID + " " + child.getCourse().getCourseNum() + " " + child.getType() + " " + child.getFaculty().get(0).getName() + " ]";
						System.out.print(output);
					}
				}
				System.out.println();
			}
			startTime = 830;
		}
		System.out.println("No of slots: " + nrSlots);
		System.out.println("No of events: " + nrEvents);
	}

	 public void printSetupConfig() {
		 	System.out.println("\n\n\nGENETIC ALGORITHM SETUP");
		    System.out.println("Desired fitness: " + TARGET_FITNESS);
		    System.out.println("Population size: " + MAX_POPULATION_SIZE);
		    System.out.println("P(Mutation) = " + ((double)MUTATION_PROB/ 1000.0d * 100) + "%");
		    System.out.println("P(Crossover) = " + ((double)CROSSOVER_PROB / 1000.0d * 100) + "%");
		    
		    System.out.println("\nVENUE DATA");
		    Map<Integer, Venue> venueList = utility.getVenues();
		    for(int key : venueList.keySet()){
		    	System.out.println(utility.getVenues().get(key).toString());
		    }
		    
		    System.out.println("\nCOURSE DATA");
		    Map<Integer, Course> courseList = utility.getCourses();
		    for(int key : courseList.keySet()){
		    		System.out.println(utility.getCourses().get(key).toString());
		    }
		  
		    System.out.println("\nFACULTY DATA");
		    Map<Integer, Faculty> facList = utility.getFaculty();
		    for(int key : facList.keySet()){
		    		System.out.println(utility.getFaculty().get(key).toString2(utility.getAvgWLU()));
		    }
		    
		    System.out.println("\nGROUPING DATA");
		    Map<Integer, Grouping> groupList = utility.getGrouping();
		    for(int key : groupList.keySet()){
		    	System.out.println(utility.getGrouping().get(key).toString());
		    }
		    
		    System.out.println("\nEVENTS DATA");
		    Map<Integer, Event> eventList = utility.getEvents();
		    for(int key : eventList.keySet()) {
		    		System.out.println(utility.getEvents().get(key).toString());
		    }
		    
		    System.out.println("Setup Completed!\n");
		  }

	public String printTimetable() {
		String result = "BEST TIMETABLE DATA\n";
		Timetable bestTimetable = utility.getBestTimetable();
		int nrSlots =0, nrEvents = 0, startTime = 830;
		ArrayList<Integer> eventList = new ArrayList<Integer>();
		for(VenueSchedule vs : bestTimetable.getVenueSchedule()){
			result += "---------------------------------------------------------------------------------------------";
			result += "\nVenue: " + vs.getVenue().getRoomName() + "\t Capacity: "+ vs.getVenue().getCapacity() + "\n";

			for(int slot = 0; slot < VenueSchedule.NO_OF_SLOT; slot++){
				result += startTime +"\t";
				startTime+=100;
				for(int day = 0; day < VenueSchedule.NO_OF_DAYS; day++){
					int eventID = vs.getEvent(day, slot);
					if(eventID > nrEvents){
						nrEvents = eventID;
					}
					nrSlots++;
					if (eventID == 0){
						result += "[\t" + eventID + "\t]";
					}
					else{
						Event child = utility.getEvents(eventID);
						eventList.add(eventID);
						String output = "[ " + child.getCourse().getCourseNum() + " " + child.getFaculty().get(0).getName()+ " ]";	
						output = "[ " + eventID + " " + child.getCourse().getCourseNum() + " " + child.getType() + " " + child.getFaculty().get(0).getName() + "\t]";
						result += output;
					}
				}
				result += "\n";
			}
			startTime = 830;
		}
		result += "\nNo of slots: " + nrSlots;
		result += "\nNo of events: " + nrEvents;
		return result;
	}

	public String printFacDayWorked() {
		// TODO Auto-generated method stub
		String result = "FACULTY WORKING DAYS DATA\n";
		Map<Integer, Faculty> facList = utility.getFaculty();
	    for(int key : facList.keySet()){
	    		result += utility.getFaculty().get(key).toString4() + "\n";
	    }
		return result;
	}
	
	public String printGroupDayWorked() {
		String result = "GROUPING WORKING DAYS DATA\n";
		Map<Integer, Grouping> groupList = utility.getGrouping();
	    for(int key : groupList.keySet()){
	    		result += utility.getGrouping().get(key).toString2() + "\n";
	    }
		return result;
	}

	public String printHrDist() {
		String result = "GROUPING HR DIST DATA\n";
		Map<Integer, Faculty> facList = utility.getFaculty();
	    for(int key : facList.keySet()){
	    		result += utility.getFaculty().get(key).toString4() + "\n";
	    }
		return result;
	}
	

}