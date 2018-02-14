package fypScheduling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;


import fypScheduling.Event.Type;

public class StimulatedAnnealing {

	private Utilities utility;
	private SAPopulation baseSolution = new SAPopulation();
	private SAPopulation bestSolution = null;

	private Map<Integer, Preference> lecPreferenceList;
	private Map<Integer, Preference> tutPreferenceList;
	private Map<Integer, Preference> labPreferenceList;

	private double temperature = 100;
	private double alpha = 0.99;

    HashMap<String, Course> courseNumList = new HashMap<String, Course>();
    HashMap<String, Faculty> facNameList = new HashMap<String, Faculty>();

	
	public StimulatedAnnealing(){
		utility = new Utilities();
		lecPreferenceList = new HashMap<Integer, Preference>();
		tutPreferenceList = new HashMap<Integer, Preference>();
		labPreferenceList = new HashMap<Integer, Preference>();
	}
		
	public Utilities getUtilites() {
		return utility;
	}
	/*
	 * SETUP
	 */	
	public void loadDatabase(String size){
		try{
			//connection to database
			Class.forName("com.mysql.jdbc.Driver");
			Connection myConn;
			if (size == "Test"){
				myConn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:8889/fypSchedulingTest", "root", "root");
			}
			else if(size == "Small"){
				//TODO : WHEN DATASET IS READY - SMALL
				myConn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:8889/fypScheduling", "root", "root");

			}
			else if (size == "Medium"){
				//TODO : WHEN DATASET IS READY - MEDIUM
				myConn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:8889/fypScheduling", "root", "root");
			}
			else{
				//TODO : WHEN DATASET IS READY - LARGE
				myConn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:8889/fypScheduling", "root", "root");
			}
			
			//create statement 
			Statement myStmt = (Statement) myConn.createStatement();
			
			//execute sql query
			loadCourse(myStmt);
			loadFaculty(myStmt);
			loadGrouping(myStmt);
			loadVenues(myStmt);
			loadHistory(myStmt);
			loadPreferenceList();
			
			//utility.addEvents();
			baseSolution.createRandomIndividuals(utility);
			updateWLUAssignedCourse(baseSolution.getList());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
 	
	private void loadPreferenceList() {
		int counter;
		Ranking rankChild;
		for(Faculty child : utility.getFaculty().values()){	
			// sort lec
			counter = 1;
			for(Course courseChild :  child.getCourses()){
				if (courseChild!=null){
					rankChild = new Ranking(child.getId(), counter);
					lecPreferenceList.get(courseChild.getId()).getRankList().add(rankChild);
					lecPreferenceList.get(courseChild.getId()).sortRank();
					counter++;
				}
			}
			
			// sort tut
			counter = 1;
			for(Course courseChild : child.getTutCourses()){
				if (courseChild!=null){
					rankChild = new Ranking(child.getId(), counter);
					tutPreferenceList.get(courseChild.getId()).getRankList().add(rankChild);
					tutPreferenceList.get(courseChild.getId()).sortRank();
					counter++;
				}
			}
		
			// sort lab
			counter = 1;
			for(Course courseChild : child.getCourses()){
				if (courseChild!=null){
					rankChild = new Ranking(child.getId(), counter);
					labPreferenceList.get(courseChild.getId()).getRankList().add(rankChild);
					labPreferenceList.get(courseChild.getId()).sortRank();
					counter++;
				}
			}		
		}
	}

	private void loadVenues(Statement myStmt) {
		//execute sql query
		try {
			ResultSet myRs = myStmt.executeQuery("select * from Venues");
			//results set
			while (myRs.next()) {
				String name = myRs.getString("room_name");
				int cap = myRs.getInt(2);
				int typeNo = myRs.getInt(3);
				Event.Type type = Event.setType(typeNo);
				
				Venue child = new Venue(name, cap, type);
				utility.addVenues(child);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	private void loadGrouping(Statement myStmt) {
		// TODO Auto-generated method stub
		try{
			ResultSet myRs = myStmt.executeQuery("select * from Grouping");
			while (myRs.next()) {
				String name = myRs.getString("group_name");
				int size = myRs.getInt(2);
				int year = myRs.getInt(4);
				Grouping child = new Grouping(name, size, year);
				
				// Tokeniser here to add courses
				String[] courseList = myRs.getString("courses_num").split(";");
				for(String courseNum : courseList){
					Course temp = courseNumList.get(courseNum);
					child.addCourse(temp);
				}
				utility.addGrouping(child);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	private void loadFaculty(Statement myStmt) {
		//execute sql query
		try{
			ResultSet myRs = myStmt.executeQuery("select * from Faculty");
			//results set
			while (myRs.next()) {
				Faculty child;
				String name = myRs.getString("name");
				int appt = myRs.getInt(5);
				int adminAppt = myRs.getInt(6);
				int serviceHr = myRs.getInt(7);
				child = new Faculty(name, appt,adminAppt, serviceHr);

				// Tokeniser here to add LEC courses taught
				String list = myRs.getString("courses_num");
				String[] courseList = list.split(";");
				
				for(String courseNum : courseList){
					Course temp = courseNumList.get(courseNum);
					child.addCourse(temp);
				}
				
				// Tokeniser here to add TUT courses taught
				String list2 = myRs.getString("tut_course");
				String[] courseList2 = list2.split(";");
				for(String courseNum : courseList2){
					Course temp = courseNumList.get(courseNum);
					child.addTutCourse(temp);
				}

				// Tokeniser here to add LAB courses taught
				String list3 = myRs.getString("lab_course");
				String[] courseList3 = list3.split(";");
				for(String courseNum : courseList3){
					Course temp = courseNumList.get(courseNum);
					child.addLabCourse(temp);
				}
				utility.addFaculty(child);
				facNameList.put(child.getName(), child);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	private void loadCourse(Statement myStmt) {
		//execute sql query
		try {
			ResultSet myRs = myStmt.executeQuery("select * from Courses");
			//results set
			while (myRs.next()) {
				Course child;
				String courseNum = myRs.getString("course_num");
				String courseName = myRs.getString("course_name");
				int noOfLecs = myRs.getInt(3);
				int noOfTuts = myRs.getInt(4);
				int noOfLabs= myRs.getInt(5);
				child = new Course(courseNum, courseName, noOfLecs, noOfTuts, noOfLabs);
				courseNumList.put(courseNum, child);
				utility.addCourses(child);
				
				Preference newLecCourse = new Preference(child);
				Preference newTutCourse = new Preference(child);
				Preference newLabCourse = new Preference(child);	
				lecPreferenceList.put(child.getId(), newLecCourse);
				tutPreferenceList.put(child.getId(), newTutCourse);
				labPreferenceList.put(child.getId(), newLabCourse);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void loadHistory(Statement myStmt) {
		//execute sql query
		try {
			ResultSet myRs = myStmt.executeQuery("select * from history");
			//results set
			while (myRs.next()) {
				History child;
				String courseNum = myRs.getString("course_num");
				Course course = courseNumList.get(courseNum);
				child = new History(course);
				
				String list = myRs.getString("history");
				String[] facIDList = list.split(";");
				
				for(String facName : facIDList){
					Faculty fac = facNameList.get(facName);
					child.addFac(fac);
				}
				utility.addHistory(child); 
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * SA Algo
	 */
	public void runSAAlgo(){		
		GUI.textbox.setText("Temperature: " + temperature + "\tAlpha: " + alpha);
	  	GUI.textbox.append("\nRunning Stimulated Algorithm. Pls wait...");
		bestSolution = new SAPopulation(baseSolution.getList());		
		
		// Get Worst WLU from baseSolution
		double baseWorstWLU = utility.getWorstWLUAllocated();
		double optimalWorstWLU = baseWorstWLU;
				
		int counter = 0;
		while(temperature > 1){		
			SAPopulation proposed = new SAPopulation(baseSolution.getList());
			
			//SWAP
			proposed.populateNewSolution(utility, lecPreferenceList, tutPreferenceList, labPreferenceList);
			
			// Reset Faculty Assignment info and recalculate WLU allocated
			updateWLUAssignedCourse(proposed.getList());
			
			// Get Worst WLU from proposeSolution
			double newWorstWLU = utility.getWorstWLUAllocated();
			System.out.println(counter + "\tBase: " + baseWorstWLU + "\t\tProposed: " + newWorstWLU + "\t\tOptimal: " + optimalWorstWLU);

			//ACCEPTANCE POLICY
			double randGen = randomDouble();
			if (acceptPolicy(baseWorstWLU, newWorstWLU, temperature) > randGen) {
				baseWorstWLU = newWorstWLU;	
				baseSolution = new SAPopulation(proposed.getList());
			}
			
			// IF PROPOSED SOLUTION IS BETTER, STORE IT
			if(baseWorstWLU < optimalWorstWLU ) {
				bestSolution = new SAPopulation(baseSolution.getList());
				updateWLUAssignedCourse(proposed.getList());
				optimalWorstWLU = utility.getWorstWLUAllocated();

			}
			
			//COOLING
			temperature *=alpha;
			counter++;
		}			
		// display output
		displayFinalAllocation();
	}
	
	
	private void updateWLUAssignedCourse(LinkedList<Event> target){	

		for (Faculty child : utility.getFaculty().values()){
			// reset WLU Allocated to each fac memeber @utitlity
			child.resetWLUAllocated();
			// reset courses assigned to each fac memeber @utitlity
			child.resetAssignedContained();
		}

		// Compute the amount of assigned course to each faculty
		for (Event child : target){
			int courseID = child.getCourse().getId();
			Course course = child.getCourse();
			
			for(Faculty facChild : child.getFaculty()){
				int facID = facChild.getId();
				
				if (utility.getFaculty().get(facID).assignedContained(courseID)){
					utility.getFaculty().get(facID).getAssignedCourse().add(course);
				}
			}
		}
		
		// recalculate events with novelity factor in
		for(Event child : target) {
			String key = child.getCourse().getCourseNum();
			ArrayList<Faculty> facList  = child.getFaculty();
			
			double novelity = 1;
			if (!utility.getHistory().containsKey(key)){
				novelity = 2;
				child.calculateWLU(novelity);
			}else {
				History history = utility.getHistory().get(key);
				novelity = history.findFac(facList);
			}	
			child.calculateWLU(novelity);
		}
		
		// assign wlu to fac
		for (Event child : target){
			double wlu;
			if (child.getFaculty().size() > 1){
				wlu = child.getWLU() / child.getFaculty().size();
			}
			else{
				wlu = child.getWLU();
			}
			
			// Update Faculty WLU Allocated
			for (Faculty facChild : child.getFaculty()){
				utility.getFaculty().get(facChild.getId()).addWLU(wlu);
			}
		}

	}
		
	public void testSAAlgo() {
		bestSolution = new SAPopulation(baseSolution.getList());		
		
		// Get Worst WLU from baseSolution
		double baseWorstWLU = utility.getWorstWLUAllocated();
		double optimalWorstWLU = baseWorstWLU;
		SAPopulation proposed = new SAPopulation(baseSolution.getList());
		
		proposed.populateNewSolution2(utility, lecPreferenceList, tutPreferenceList, labPreferenceList);

		// Reset Faculty Assignment info and recalculate WLU allocated
		updateWLUAssignedCourseTEST(proposed.getList());
		
		
		// Get Worst WLU from proposeSolution
		double newWorstWLU = utility.getWorstWLUAllocated();
		System.out.println("Base: " + baseWorstWLU + "\t\tProposed: " + newWorstWLU + "\t\tOptimal: " + optimalWorstWLU);
		
	}
	
	private void updateWLUAssignedCourseTEST(LinkedList<Event> target){	
		
		for (Faculty child : utility.getFaculty().values()){
			// reset WLU Allocated to each fac memeber @utitlity
			child.resetWLUAllocated();
			// reset courses assigned to each fac memeber @utitlity
			child.resetAssignedContained();
		}

		// Compute the amount of assigned course to each faculty
		for (Event child : target){
			int courseID = child.getCourse().getId();
			Course course = child.getCourse();
			
			for(Faculty facChild : child.getFaculty()){
				int facID = facChild.getId();
				
				if (utility.getFaculty().get(facID).assignedContained(courseID)){
					utility.getFaculty().get(facID).getAssignedCourse().add(course);
				}
			}
		}
		
		System.out.println("BEFORE NOVELTY");
		for(Event child: target) {
			child.calculateWLU(1);
			System.out.println(child.toString());
		}
		
		System.out.println("\n\nAFTER NOVELTY");

		// recalculate events with novelity factor in
		for(Event child : target) {
			String key = child.getCourse().getCourseNum();
			ArrayList<Faculty> facList  = child.getFaculty();
			
			double novelity = 1;
			if (!utility.getHistory().containsKey(key)){
				novelity = 2;
				child.calculateWLU(novelity);
				System.out.println(child.getCourse().getCourseNum() + " is not in history");
			}else {
				History history = utility.getHistory().get(key);
				novelity = history.findFac(facList);
				System.out.println(child.getId() + " \tNov: " + novelity);
			}	
			child.calculateWLU(novelity);
			System.out.println(child.toString());
		}
		
		// assign wlu to fac
		for (Event child : target){
			double wlu;
			if (child.getFaculty().size() > 1){
				wlu = child.getWLU() / child.getFaculty().size();
			}
			else{
				wlu = child.getWLU();
			}
			
			// Update Faculty WLU Allocated
			for (Faculty facChild : child.getFaculty()){
				utility.getFaculty().get(facChild.getId()).addWLU(wlu);
			}
		}

	}

	private double randomDouble(){
		Random r = new Random();
		return r.nextInt(1000) / 1000;
	}
		
	private double acceptPolicy(double base, double proposed, double temperature){
		if (proposed > base)
			// if base is lower than proposed, adjust p
			return Math.exp((base-proposed)/ temperature);
		else
			// accept new result if base is higher than proposed
			return 1;
	}
		
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	public void setTemperture(double temp) {
		this.temperature = temp;
	}
	
	public void printSetupConfig(){
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
	    	System.out.println(utility.getFaculty().get(key).toString());
	    }
	    
	    System.out.println("\nGROUPING DATA");
	    Map<Integer, Grouping> groupList = utility.getGrouping();
	    for(int key : groupList.keySet()){
	    	System.out.println(utility.getGrouping().get(key).toString());
	    }
	    System.out.println("\nEvent Data");
	    for (Event child : baseSolution.getList()) {
	    		System.out.println(child.toString());
	    }
		System.out.println("Setup Completed!\n");
	}

	private void displayFinalAllocation() {
		
		System.out.println("\n\nEvent Allocation Result");
		updateWLUAssignedCourse(bestSolution.getList());
		for (Event child : bestSolution.getList()) {
			System.out.println(child.toString());
			utility.getEvents().put(child.getId(), child);
		}
		
		System.out.println("\n\nFaculty Assignment Result");
		for (Faculty child: utility.getFaculty().values()) {
			System.out.println(child.toString2());
		}
		System.out.println("Worst WLU: " + utility.getWorstWLUAllocated());		
		
		
		String result = "\nEvent Allocation Result";
		result += "Worst WLU: " + utility.getWorstWLUAllocated() + "\nFaculty Assignment Result\n";
		
		for (Faculty child: utility.getFaculty().values()) {
			result += child.toString2() + "\n";
		}
		
		result+= "\n\nEvent Allocation Result\n";
		for (Event child : bestSolution.getList()) {
			result += child.toString() + "\n";
		}
		
		GUI.textbox.append(result);
	}

	public String printCourses() {
		String result = "COURSE DATA\n";
		
	    Map<Integer, Course> courseList = utility.getCourses();
	    for(int key : courseList.keySet()){
	    		result += utility.getCourses().get(key).toString() +"\n";
	    }
		return result;
	}

	public String printFacultyPreference() {
		String result = "FACULTY DATA\n";
		
		Map<Integer, Faculty> facList = utility.getFaculty();
	    for(int key : facList.keySet()){
	    		result += utility.getFaculty().get(key).toString() + "\n";
	    }
		return result;
	}
	
	public String printFaculty() {
		String result = "FACULTY DATA\n";
		
		Map<Integer, Faculty> facList = utility.getFaculty();
	    for(int key : facList.keySet()){
	    		result += utility.getFaculty().get(key).toString2() + "\n";
	    }
	    
	    double worstWLU = utility.getWorstWLUAllocated();
	    double avgWLU = utility.getAvgWLU();
	    result += "\n\nAvg WLU: " + avgWLU + "\tWorst WLU Allocated: " + worstWLU;
		return result;
	}

	public String printGrouping() {
		String result = "GROUPING DATA\n";

	    Map<Integer, Grouping> groupList = utility.getGrouping();
	    for(int key : groupList.keySet()){
	    		result+=utility.getGrouping().get(key).toString() + "\n";
	    }
		return result;
	}

	public String printVenues() {
		String result = "VENUES DATA\n";
		
	    Map<Integer, Venue> venueList = utility.getVenues();
	    for(int key : venueList.keySet()){
	    		result += utility.getVenues().get(key).toString() + "\n";
	    }
		return result;
	}

	public String printHistory() {
		String result = "HISTORIC DATA\n";

	    Map<String, History> historyList = utility.getHistory();
	    for(String key : historyList.keySet()){
	    		result += utility.getHistory().get(key).toString() + "\n";
	    }
		return result;
	}

	public String printEvents() {
		String result = "EVENTS DATA\n";
	    Map<Integer, Event> eventList = utility.getEvents();
	    for(int key : eventList.keySet()){
	    		result += utility.getEvents().get(key).toString() + "\n";
	    }
		return result;
	}
	
}
