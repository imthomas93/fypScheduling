package fypScheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fypScheduling.Event.Type;

public class Utilities {

	private Map<Integer, Venue> rooms;
	private Map<Integer, Course> courses;
	private Map<Integer, Grouping> grouping;
	private Map<Integer, Faculty> faculty;
	private Map<Integer, Event> events;
	private ArrayList<Integer> eventList;
	private Timetable bestTimetable;
	

	private Map<String, History> history;
	private Map<Integer, StudentPreference> studentPreference; 

	public Utilities(){
		eventList = new ArrayList<Integer>();
		rooms = new HashMap<Integer, Venue>();
		courses = new HashMap<Integer, Course>();
		grouping = new HashMap<Integer, Grouping>();
		faculty = new HashMap<Integer, Faculty>();
		events = new HashMap<Integer, Event>();
		
		history = new HashMap<String, History>();
		setStudentPreference(new HashMap<Integer, StudentPreference>());
	}

	public void addVenues(Venue room){
		this.rooms.put(room.getId(), room);
	}
	
	public void addEvent(Event event) {
		this.events.put(event.getId(), event);
	}
	
	public int getNoOfVenues(){
		return this.rooms.size();
	}
	public Map<Integer, Venue> getVenues(){
		return this.rooms;
	}

	public Map<Integer, Course> getCourses() {
		return this.courses;
	}

	public void addCourses(Course course) {
		this.courses.put(course.getId(), course);
	}
	
	public void addHistory(History history) {
		this.history.put(history.getCourse().getCourseNum(), history);
	}
	
	public void setCoursesList(Map<Integer, Course> courses){
		this.courses = courses;
	}

	public Map<Integer, Grouping> getGrouping() {
		return grouping;
	}

	public void addGrouping(Grouping grouping) {
		this.grouping.put(grouping.getId(), grouping);
	}

	public Map<Integer, Faculty> getFaculty() {
		return faculty;
	}

	public void addFaculty(Faculty faculty) {
		this.faculty.put(faculty.getId(), faculty);
	}

	public Map<Integer, Event> getEvents() {
		return events;
	}
	
	public Event getEvents(int key) {
		return events.get(key);
	}
	
	public void setEvents(Map<Integer, Event> events){
		this.events = events;
	}
	
	public int getRandomEventList(){
		Random rand = new Random();
		int result = eventList.get(rand.nextInt(eventList.size()));
		return result;
	}
	
	public double getTotalWLU(){
		double result = 0;
		for (Event child : events.values()){
			result += child.getWLU();
		}
		return result;
	}
	
	public double getTotalTeachingContribution(){
		double result = 0;
		for (Faculty child : faculty.values()){
			result += child.getTeachingContribution();
		}
		return result;
	}
	
	public void addEvents(){
		int eventID = 1, eventDuration;
		Random r = new Random(System.currentTimeMillis());
		
		for (Grouping groupChild : grouping.values()){
			for (Course courseChild : groupChild.getCourses()){
				
				// assign ONLY 1 faculty to this course (can be further modified)	
				ArrayList<Faculty> possibleFaculty = new ArrayList<Faculty>();
				for (Faculty facChild : faculty.values()){
					
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
					events.put(event.getId(), event);
					eventList.add(event.getId());
					
				}
				else{
					eventDuration = 1;
					for(int i = 0; i < courseChild.getNoOfLecs(); i++){
						Event event = new Event(Type.LEC, groupChild.getSize(), 
								chosenFaculty, courseChild, 
								groupChild, eventID, eventDuration);
						
						eventID++;
						events.put(event.getId(), event);
						eventList.add(event.getId());
					}
				}
				
				// TUT
				eventDuration = courseChild.getNoOfTuts();
				if (eventDuration !=0){
					possibleFaculty = new ArrayList<Faculty>();
					for (Faculty facChild : faculty.values()){
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
						events.put(event.getId(), event);
						eventList.add(event.getId());
						groupSize -= eventSize;
					}
				}
							
				// LAB
				eventDuration = courseChild.getNoOfLabs();
				if(eventDuration !=0){
					possibleFaculty = new ArrayList<Faculty>();
					for (Faculty facChild : faculty.values()){
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
						events.put(event.getId(), event);
						eventList.add(event.getId());
						groupSize -= eventSize;
					}
				}		
				
			}
		}
	}

	public double getWorstWLUAllocated() {
		double result = 0;
		
		for(Faculty child : faculty.values()) {
			double effiectiveTC = this.getAvgWLU() * child.getTeachingContribution();
			double allocatedWLU = child.getAllocatedWLU();
			
			double distribtion = ((allocatedWLU-effiectiveTC)/effiectiveTC) * 100;
			if (distribtion <0) {
				result +=0;
			}
			else if (distribtion < 10) {
				result +=10;
			}
			else if (distribtion < 20) {
				result +=20;
			}
			else if (distribtion < 20) {
				result +=30;
			}
			else if (distribtion <40) {
				result +=40;
			}else {
				result +=80;
			}
		}
		
		return result;
	}
	
	public ArrayList<Course> getWorstAllocatedEvents(){
		double worst = 0;
		int facID = 0;
		
		for(Faculty child : faculty.values()) {
			if (child.getAllocatedWLU() > worst) {
				worst = child.getAllocatedWLU();
				facID = child.getId();
			}
		}
		
		Faculty child = faculty.get(facID);
		ArrayList<Course> courseList = child.getAssignedCourse();
	
		return courseList;
	}
	
	private int randomInt(int low, int high){
		Random r = new Random();
		double result = (high-low) * r.nextDouble() + low;
		return (int) result;
	}
	
	public ArrayList<Course> getWorstAllocatedEvents2(){
		double worst = 0;
		int facID = 0;
		Queue queue = new Queue(10);
		
		for(Faculty child : faculty.values()) {
			double tc = child.getTeachingContribution();
			double avg = getAvgWLU();
			double optimalWLU = tc*avg;
			double currentWLU = child.getAllocatedWLU();
			
			double distribution = ((currentWLU - optimalWLU) / optimalWLU) * 100;
			if (distribution > 40) {
				facID = child.getId();
				queue.insert(facID);
			}
		}
		
		if (queue.isEmpty()) {
			for(Faculty child : faculty.values()) {
				double tc = child.getTeachingContribution();
				double avg = getAvgWLU();
				double optimalWLU = tc*avg;
				double currentWLU = child.getAllocatedWLU();
				
				double distribution = ((currentWLU - optimalWLU) / optimalWLU) * 100;
				if (distribution > 30) {
					facID = child.getId();
					queue.insert(facID);
				}
			}
		}
		
		int pos = randomInt(0, queue.size());
		
		while(pos!=0) {
			queue.remove();
			pos--;
		}
		
		facID = queue.peekFront();
		
		
		Faculty child = faculty.get(facID);
		ArrayList<Course> courseList = child.getAssignedCourse();
	
		return courseList;
	}

	public Map<Integer, StudentPreference> getStudentPreference() {
		return studentPreference;
	}

	public void setStudentPreference(Map<Integer, StudentPreference> studentPreference) {
		this.studentPreference = studentPreference;
	}

	public Map<String, History> getHistory() {
		return history;
	}

	public void setHistory(Map<String, History> history) {
		this.history = history;
	}

	public double getAvgWLU() {
		double result = 0;
		for(Faculty child : faculty.values()) {
			result += child.getAllocatedWLU();
		}		
		result = result / getTotalTeachingContribution();
		
		return result;
	}
	


	public int getOverloadedFac() {
		// TODO Auto-generated method stub
		double avgWLU = getAvgWLU();
		double effectiveWLU;
		int result = 0;
		
		for(Faculty child : faculty.values()) {
			effectiveWLU = child.getTeachingContribution() * avgWLU;
			if (child.getAllocatedWLU() > effectiveWLU) {
				result++;
			}
		}
		
		return result;
	}

	public Timetable getBestTimetable() {
		return bestTimetable;
	}

	public void setBestTimetable(Timetable bestTimetable) {
		this.bestTimetable = bestTimetable;
	}

	public double getLecTotalWLU() {
		double result = 0;
		for (Event child : events.values()){
			if (child.getType() == Type.LEC)
				result += child.getWLU();
		}
		return result;
	}
	
	public double getTutTotalWLU() {
		double result = 0;
		for (Event child : events.values()){
			if (child.getType() == Type.TUT)
				result += child.getWLU();
		}
		return result;
	}
	
	public double getLabTotalWLU() {
		double result = 0;
		for (Event child : events.values()){
			if (child.getType() == Type.LAB)
				result += child.getWLU();
		}
		return result;
	}

	public double getAvgDistribution(double avgWLU) {
		double result = 0;
		double effectiveWLU, distribution = 0;
		for(Faculty child : faculty.values()) {
			effectiveWLU = child.getTeachingContribution() * avgWLU;
			distribution = ((child.getAllocatedWLU() - effectiveWLU) / effectiveWLU) * 100;
			result = result + (distribution);
		}
		result = result / faculty.size();
		return result;
	}


	
}