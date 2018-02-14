package fypScheduling;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import fypScheduling.Event.Type;

public class SAPopulation {
	
	private LinkedList<Event> individuals;
	
	public SAPopulation(){
		individuals = new LinkedList<Event>();
	}
	
	public SAPopulation(LinkedList<Event> list) {
		individuals = new LinkedList<Event>();
		for (Event child : list) {
			individuals.add(child.deepClone());
		}
	}
	
	public void populateNewSolution(Utilities utility, Map<Integer, Preference> lecPreferenceList, Map<Integer, Preference> tutPreferenceList, Map<Integer, Preference> labPreferenceList) {
		// Random generate a solution and get the event
		int randSwapID = randomInt(0, individuals.size()); 
		Event e = individuals.get(randSwapID);
		double avgTeachingWkLoad = utility.getAvgWLU();
		System.out.println("Chosen Event to swap: " + randSwapID+1 + "\t\t" + e.getType());
		
		
		// IF EVENT IS A LECTURE
		if (e.getType() == Type.LEC){
		
			// Get preference list
			int pos = 0, selectedFacID =-1;
			double highestTreshold = 0, lowestTreshold = 10000;
			Preference preferenceChild = lecPreferenceList.get(e.getCourse().getId());				
	
			// from the preference list, compute the FI of each possible faculty
			for(Ranking rankChild : preferenceChild.getRankList()) {
				// calculate FI = fac current load + fac preference + #ofCourseTaughtbyFac + history(OPTIONAL FOR NOW)
				
				//Part 1: Compile Rank
				double result = rankChild.getRank();
				
				// PART 2: Compile #ofCourseTaughtbyFac
				int facID = rankChild.getFacID();
				double assignedCourseNo = utility.getFaculty().get(facID).getAssignedCourse().size();
				if (assignedCourseNo >3){
					result+=10;
				}
				
				// PART 3: Compile fac current WLU allocated
				Faculty facSelected = utility.getFaculty().get(facID);
				double currentAllocatedWLU = facSelected.getAllocatedWLU();
				result += currentAllocatedWLU;
				
				/*
				// PART 4: Compile fac optimal wkload
				*/
				double optimalWLU = avgTeachingWkLoad * facSelected.getTeachingContribution();
				if (currentAllocatedWLU > optimalWLU) {
					result += 10;
				}
				
				rankChild.setFI(result);
				
				
				// PART 5: Compile student preference
				//result += utility.getStudentPreference().get(e.getCourse().getId()).getPreferenceScore(facID);
			}
			
			// Get the Lowest FI to swap in
			for(Ranking rankChild : preferenceChild.getRankList()) {
				double FIScore = rankChild.getFI();
				if (FIScore < lowestTreshold) {
					selectedFacID = rankChild.getFacID();
					lowestTreshold = FIScore;
				}
			}
	
			// Pick the Higest FI to swap out (EXISITING)
			int counter = 0;
			pos = 0;
			boolean canSwap = e.doesNotCrash(selectedFacID);
			if (canSwap) {
				for(Faculty facChild : e.getFaculty()) {
					int currID = facChild.getId();
					if (currID == selectedFacID) {
						selectedFacID = -1;
					}else {
						for(Ranking rankChild : preferenceChild.getRankList()) {
							int facID = rankChild.getFacID();
							double tempScore = rankChild.getFI();
							if (currID == facID && tempScore > highestTreshold) {
								pos = counter;
								highestTreshold = tempScore;
							}
						}
					}
					counter++;
				}
				// Swap the faculty teaching
				if(selectedFacID != -1) {
					Faculty newFac = utility.getFaculty().get(selectedFacID);
					e.swapFaculty(pos, newFac);
				}
			}
		
			// reset FI
			for(Ranking rankChild : preferenceChild.getRankList()) {
				rankChild.setFI(0);
			}	
		}
		// IF EVENT IS A TUT
		else if (e.getType() == Type.TUT){
			// Get preference list
			int newSelectedFac =-1;
			double lowestTreshold = 10000;
			Preference preferenceChild = tutPreferenceList.get(e.getCourse().getId());				
	
			// from the preference list, compute the FI of each possible faculty
			for(Ranking rankChild : preferenceChild.getRankList()) {
				// calculate FI = fac current load + fac preference + #ofCourseTaughtbyFac + history(OPTIONAL FOR NOW)
				
				//Part 1: Compile Rank
				double result = rankChild.getRank();
				
				// PART 2: Compile #ofCourseTaughtbyFac
				int facID = rankChild.getFacID();
				double assignedCourseNo = utility.getFaculty().get(facID).getAssignedCourse().size();
				if (assignedCourseNo >3){
					result+=10;
				}
				
				// PART 3: Compile fac current WLU allocated
				Faculty facSelected = utility.getFaculty().get(facID);
				double currentAllocatedWLU = facSelected.getAllocatedWLU();
				result += currentAllocatedWLU;
				
				// PART 4: Compile fac optimal wkload
				double optimalWLU = avgTeachingWkLoad * facSelected.getTeachingContribution();
				if (currentAllocatedWLU > optimalWLU) {
					result += 10;
				}
			}
			
			// Get the Lowest FI to swap in
			for(Ranking rankChild : preferenceChild.getRankList()) {
				double tempFIScore = rankChild.getFI();
				if (tempFIScore < lowestTreshold) {
						newSelectedFac = rankChild.getFacID();
						lowestTreshold = tempFIScore;
				}
			}
			
			//Swap it
			Faculty newFac = utility.getFaculty().get(newSelectedFac);
			e.swapFaculty(0, newFac);
			
			// reset FI
			for(Ranking rankChild : preferenceChild.getRankList()) {
				rankChild.setFI(0);
			}
		}
		// IF EVENT IS A LAB
		else {
			// Get preference list
			int newSelectedFac =-1;
			double lowestTreshold = 10000;
			Preference preferenceChild = labPreferenceList.get(e.getCourse().getId());				
	
			// from the preference list, compute the FI of each possible faculty
			for(Ranking rankChild : preferenceChild.getRankList()) {
				// calculate FI = fac current load + fac preference + #ofCourseTaughtbyFac + history(OPTIONAL FOR NOW)
				
				//Part 1: Compile Rank
				double result = rankChild.getRank();
				
				// PART 2: Compile #ofCourseTaughtbyFac
				int facID = rankChild.getFacID();
				double assignedCourseNo = utility.getFaculty().get(facID).getAssignedCourse().size();
				if (assignedCourseNo >3){
					result+=10;
				}
				
				// PART 3: Compile fac current WLU allocated
				Faculty facSelected = utility.getFaculty().get(facID);
				double currentAllocatedWLU = facSelected.getAllocatedWLU();
				result += currentAllocatedWLU;
				
				// PART 4: Compile fac optimal wkload
				double optimalWLU = avgTeachingWkLoad * facSelected.getTeachingContribution();
				if (currentAllocatedWLU > optimalWLU) {
					result += 10;
				}
			}
						
			// Get the Lowest FI to swap in
			for(Ranking rankChild : preferenceChild.getRankList()) {
				double tempFIScore = rankChild.getFI();
				if (tempFIScore < lowestTreshold) {
						newSelectedFac = rankChild.getFacID();
						lowestTreshold = tempFIScore;
				}
			}
						
			// Swap it
			Faculty newFac = utility.getFaculty().get(newSelectedFac);
			e.swapFaculty(0, newFac);
			
			// Reset FI
			for(Ranking rankChild : preferenceChild.getRankList()) {
				rankChild.setFI(0);
			}		
		}
	}
	
	
	public void populateNewSolution2(Utilities utility, Map<Integer, Preference> lecPreferenceList, Map<Integer, Preference> tutPreferenceList, Map<Integer, Preference> labPreferenceList) {
		// Random generate a solution and get the event
		System.out.println("Event size: " + individuals.size());
		int randSwapID = randomInt(0, individuals.size()); 
		Event e = individuals.get(randSwapID);
		System.out.println("Chosen event: " + (randSwapID+1) + "\t\t" + e.getType());
		System.out.println(e.toString());
		
		// IF EVENT IS A LECTURE
		if (e.getType() == Type.LEC){
		
			// Get preference list
			int pos = 0, selectedFacID =-1;
			double highestTreshold = 0, lowestTreshold = 10000;
			Preference preferenceChild = lecPreferenceList.get(e.getCourse().getId());				
	
			// from the preference list, compute the FI of each possible faculty
			for(Ranking rankChild : preferenceChild.getRankList()) {
				// calculate FI = fac current load + fac preference + #ofCourseTaughtbyFac + history(OPTIONAL FOR NOW)
				
				//Part 1: Compile Rank
				double result = rankChild.getRank();
				
				// PART 2: Compile #ofCourseTaughtbyFac
				int facID = rankChild.getFacID();
				double assignedCourseNo = utility.getFaculty().get(facID).getAssignedCourse().size();
				if (assignedCourseNo >2){
					result+=10;
				}
				
				// PART 3: Compile fac current WLU allocated
				result += utility.getFaculty().get(facID).getAllocatedWLU();
				rankChild.setFI(result);
				
				// PART 4: Compile student preference
				//result += utility.getStudentPreference().get(e.getCourse().getId()).getPreferenceScore(facID);
			}

			// Get the Lowest FI to swap in
			for(Ranking rankChild : preferenceChild.getRankList()) {
				double FIScore = rankChild.getFI();
				if (FIScore < lowestTreshold) {
					selectedFacID = rankChild.getFacID();
					lowestTreshold = FIScore;
				}
			}
			
			// Pick the Higest FI to swap out (EXISITING)
			int counter = 0;
			pos = 0;
			boolean canSwap = e.doesNotCrash(selectedFacID);
			if (canSwap) {
				for(Faculty facChild : e.getFaculty()) {
					int currID = facChild.getId();
					if (currID == selectedFacID) {
						selectedFacID = -1;
					}else {
						for(Ranking rankChild : preferenceChild.getRankList()) {
							int facID = rankChild.getFacID();
							double tempScore = rankChild.getFI();
							if (currID == facID && tempScore > highestTreshold) {
								pos = counter;
								highestTreshold = tempScore;
							}
						}
					}
					counter++;
				}
				// Swap the faculty teaching
				if(selectedFacID != -1) {
					Faculty newFac = utility.getFaculty().get(selectedFacID);
					e.swapFaculty(pos, newFac);
				}
			}
			
			for(Ranking rankChild : preferenceChild.getRankList()) {
				System.out.println(rankChild.toString());
			}
			System.out.println();
			// print all the preference list
			for(Ranking rankChild : preferenceChild.getRankList()) {
				System.out.println(rankChild.toString2());
			}
			System.out.println();
			System.out.println("Chosen Fac to swap in: " + selectedFacID);
			System.out.println("Chosen pos to swap out: " + pos);
			
		
			// reset FI
			for(Ranking rankChild : preferenceChild.getRankList()) {
				rankChild.setFI(0);
			}	
		}
		// IF EVENT IS A TUT
		else if (e.getType() == Type.TUT){
			// Get preference list
			int newSelectedFac =-1;
			double lowestTreshold = 10000;
			Preference preferenceChild = tutPreferenceList.get(e.getCourse().getId());				
	
			// from the preference list, compute the FI of each possible faculty
			for(Ranking rankChild : preferenceChild.getRankList()) {
				// calculate FI = fac current load + fac preference + #ofCourseTaughtbyFac + history(OPTIONAL FOR NOW)
				
				//Part 1: Compile Rank
				double result = rankChild.getRank();
				
				// PART 2: Compile #ofCourseTaughtbyFac
				int facID = rankChild.getFacID();
				double assignedCourseNo = utility.getFaculty().get(facID).getAssignedCourse().size();
				if (assignedCourseNo >2){
					result+=10;
				}
				
				// PART 3: Compile fac current WLU allocated
				result += utility.getFaculty().get(facID).getAllocatedWLU();
				rankChild.setFI(result);
			}
			
			// Get the Lowest FI to swap in
			for(Ranking rankChild : preferenceChild.getRankList()) {
				double tempFIScore = rankChild.getFI();
				if (tempFIScore < lowestTreshold) {
						newSelectedFac = rankChild.getFacID();
						lowestTreshold = tempFIScore;
				}
			}
			for(Ranking rankChild : preferenceChild.getRankList()) {
				System.out.println(rankChild.toString());
			}
			System.out.println();
			// print all the preference list
			for(Ranking rankChild : preferenceChild.getRankList()) {
				System.out.println(rankChild.toString2());
			}
			System.out.println();
			System.out.println("Chosen Fac to swap in: " + newSelectedFac);
			
			//Swap it
			Faculty newFac = utility.getFaculty().get(newSelectedFac);
			e.swapFaculty(0, newFac);
			
			// reset FI
			for(Ranking rankChild : preferenceChild.getRankList()) {
				rankChild.setFI(0);
			}
			

		}
		// IF EVENT IS A LAB
		else {
			// Get preference list
			int newSelectedFac =-1;
			double lowestTreshold = 10000;
			Preference preferenceChild = labPreferenceList.get(e.getCourse().getId());				
	
			// from the preference list, compute the FI of each possible faculty
			for(Ranking rankChild : preferenceChild.getRankList()) {
				// calculate FI = fac current load + fac preference + #ofCourseTaughtbyFac + history(OPTIONAL FOR NOW)
				
				//Part 1: Compile Rank
				double result = rankChild.getRank();
				
				// PART 2: Compile #ofCourseTaughtbyFac
				int facID = rankChild.getFacID();
				double assignedCourseNo = utility.getFaculty().get(facID).getAssignedCourse().size();
				if (assignedCourseNo >2){
					result+=10;
				}
				
				// PART 3: Compile fac current WLU allocated
				result += utility.getFaculty().get(facID).getAllocatedWLU();
				rankChild.setFI(result);
			}
						
			// Get the Lowest FI to swap in
			for(Ranking rankChild : preferenceChild.getRankList()) {
				double tempFIScore = rankChild.getFI();
				if (tempFIScore < lowestTreshold) {
						newSelectedFac = rankChild.getFacID();
						lowestTreshold = tempFIScore;
				}
			}
						
			for(Ranking rankChild : preferenceChild.getRankList()) {
				System.out.println(rankChild.toString());
			}
			System.out.println();
			// print all the preference list
			for(Ranking rankChild : preferenceChild.getRankList()) {
				System.out.println(rankChild.toString2());
			}
			System.out.println();
			System.out.println("Chosen Fac to swap in: " + newSelectedFac);
			
			
			// Swap it
			Faculty newFac = utility.getFaculty().get(newSelectedFac);
			e.swapFaculty(0, newFac);
			
			// Reset FI
			for(Ranking rankChild : preferenceChild.getRankList()) {
				rankChild.setFI(0);
			}	
		}
	}

	private int randomInt(int low, int high){
		Random r = new Random();
		double result = (high-low) * r.nextDouble() + low;
		return (int) result;
	}	
		
	public LinkedList<Event> getList(){
		return (LinkedList<Event>) this.individuals;
	}
	
	public Event getTopIndividual(){
		return individuals.getFirst();
	}
	
	public Event getWorstIndividual(){
		return individuals.getLast();
	}
	
	public Event getIndividual(int target){
		return this.individuals.get(target);
	}
	
	public void addIndividual(Event target){
		individuals.add(target);
	}
	
	public ListIterator<Event> listIterator(){
		return individuals.listIterator();
	}
	
	public int size(){
		return individuals.size();
	}
	
	public void createRandomIndividuals(Utilities utility) {
		int eventID = 1, eventDuration;
		Random r = new Random(System.currentTimeMillis());
		
		for (Grouping groupChild : utility.getGrouping().values()){
			for (Course courseChild : groupChild.getCourses()){
				
				// assign ONLY 1 faculty to this course (can be further modified)	
				ArrayList<Faculty> possibleFaculty = new ArrayList<Faculty>();
				for (Faculty facChild : utility.getFaculty().values()){
					
					if (facChild.isTeachable(courseChild.getCourseNum()) && facChild!=null){
						possibleFaculty.add(facChild);
					}
				}
				
				//Lecture
				int rand1, rand2;
				ArrayList<Faculty> chosenFaculty = new ArrayList<Faculty>();
				if (possibleFaculty.size() !=1){
					// assign 2 prof to teach a course lec is possible pool is bigger than 1
					rand1 = r.nextInt(possibleFaculty.size());
					rand2 = r.nextInt(possibleFaculty.size());
					while(rand2==rand1){
						rand2 = r.nextInt(possibleFaculty.size());
					}
					Faculty chosen1 = possibleFaculty.get(rand1);
					chosenFaculty.add(chosen1);
					Faculty chosen2 = possibleFaculty.get(rand2);
					chosenFaculty.add(chosen2);
				}
				else{				
					// assign 1 prof to teach
					rand1 = r.nextInt(possibleFaculty.size());
					Faculty chosen1 = possibleFaculty.get(rand1);
					chosenFaculty.add(chosen1);
				}
				
				// if course is senior year, lec can be taught back-to-back 
				if(groupChild.isSeniorYear()){
					eventDuration = courseChild.getNoOfLecs();
					Event event = new Event(Type.LEC, groupChild.getSize(), 
							chosenFaculty, courseChild, 
							groupChild, eventID, eventDuration);
					
					eventID++;
					individuals.add(event);
				}
				else{
					eventDuration = 1;
					for(int i = 0; i < courseChild.getNoOfLecs(); i++){
						Event event = new Event(Type.LEC, groupChild.getSize(), 
								chosenFaculty, courseChild, 
								groupChild, eventID, eventDuration);
						
						eventID++;
						
						individuals.add(event);
					}
				}
				
				// TUT
				eventDuration = courseChild.getNoOfTuts();
				if (eventDuration !=0){
					possibleFaculty = new ArrayList<Faculty>();
					for (Faculty facChild : utility.getFaculty().values()){
						if (facChild.isTeachableTut(courseChild.getCourseNum()) && facChild!=null){
							possibleFaculty.add(facChild);
						}
					}
					int groupSize = groupChild.getSize();
					
					while (groupSize > 0){
						// temp assignment - rightfully year 4 = LT size
						int eventSize = 30;
						
						rand1 = r.nextInt(possibleFaculty.size());
						Faculty chosen = possibleFaculty.get(rand1);
						chosenFaculty = new ArrayList<Faculty>();
						chosenFaculty.add(chosen);
						Event event = new Event(Type.TUT, eventSize,
								chosenFaculty, courseChild, 
								groupChild, eventID, eventDuration);
						
						eventID++;
						individuals.add(event);
						groupSize -= eventSize;
					}
				}
							
				// LAB
				eventDuration = courseChild.getNoOfLabs();
				if(eventDuration !=0){
					possibleFaculty = new ArrayList<Faculty>();
					for (Faculty facChild : utility.getFaculty().values()){
						if (facChild.isTeachableLab(courseChild.getCourseNum()) && facChild!=null){
							possibleFaculty.add(facChild);
						}
					}
					
					int groupSize = groupChild.getSize();
					while (groupSize >0){
						// temp assignment 
						int eventSize = 50;
					
						rand1 = r.nextInt(possibleFaculty.size());						
						Faculty chosen = possibleFaculty.get(rand1);
						chosenFaculty = new ArrayList<Faculty>();
						chosenFaculty.add(chosen);
						Event event = new Event(Type.LAB, eventSize,
								chosenFaculty, courseChild, 
								groupChild, eventID,eventDuration);
						
						eventID++;
						individuals.add(event);
						groupSize -= eventSize;
					}
				}		
				
			}
		}
	}



}
