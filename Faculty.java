package fypScheduling;
import java.io.Serializable;
import java.util.ArrayList;

public class Faculty implements Serializable {

	private static int incID =0; 
	
	private int id;
	private String name;
	private ArrayList<Course> teachingCourses;
	private ArrayList<Course> tutCourses;
	private ArrayList<Course> labCourses;
	private int appt;
	private int adminAppt;
	private int serviceHr;
	private double teachingContribution;
	private double allocatedWLU;
	private ArrayList<Course> assignedCourse;
	private int[] dayOfWork;

	
	public Faculty(String name, int appt, int adminAppt, int serviceHr){
		id = incID++;
		this.name = name;
		this.setAppt(appt);
		this.setAdminAppt(adminAppt);
		this.setServiceHr(serviceHr);
		teachingCourses = new ArrayList<Course>();
		tutCourses = new ArrayList<Course>();
		labCourses = new ArrayList<Course>();
		setTeachingContribution();
		setAllocatedWLU(0);
		assignedCourse = new ArrayList<Course>();
		dayOfWork = new int[5];
		resetDayOfWork();
	}
	
	public void resetDayOfWork() {
		for (int i = 0; i <5; i++) {
			dayOfWork[i] = 0;
		}
	}
	
	public void increaseDayOfWork(int day) {
		dayOfWork[day] = dayOfWork[day] + 1;
	}
	
	public int countDaysOfWork() {
		int result = 0;
		for (int i = 0; i < 5; i++) {
			if (dayOfWork[i] != 0) {
				result++;
			}
		}
		return result;
	}
	
	public boolean isDayExceeded() {
		int result = 0;
		for (int i = 0; i < 5; i++) {
			if (dayOfWork[i] != 0) {
				result++;
			}
		}
		
		if (result >= 3) {
			return true;
		}
		
		return false;
	}
	
	public boolean isDayExceeded(int day) {
		
		if (dayOfWork[day] > 1) {
			return true;
		}
		
		return false;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Course> getCourses() {
		return teachingCourses;
	}
	
	public ArrayList<Course> getTutCourses() {
		return tutCourses;
	}
	
	public ArrayList<Course> getLabCourses() {
		return labCourses;
	}
	
	public void setCourses(ArrayList<Course> courses) {
		this.teachingCourses = courses;
	}
	
	public void setTutCourses(ArrayList<Course> courses) {
		this.tutCourses = courses;
	}
	
	public void setLabCourses(ArrayList<Course> courses) {
		this.labCourses = courses;
	}
	
	public void addCourse(Course target){
		this.teachingCourses.add(target);
	}
	
	public void addTutCourse(Course target){
		this.tutCourses.add(target);
	}
	
	public void addLabCourse(Course target){
		this.labCourses.add(target);
	}
	public int getAppt() {
		return appt;
	}

	public void setAppt(int appt) {
		this.appt = appt;
	}

	public int getAdminAppt() {
		return adminAppt;
	}

	public void setAdminAppt(int adminAppt) {
		this.adminAppt = adminAppt;
	}

	public int getServiceHr() {
		return serviceHr;
	}

	public void setServiceHr(int serviceHr) {
		this.serviceHr = serviceHr;
	}

	public double getTeachingContribution() {
		return this.teachingContribution;
	}

	public ArrayList<Course> getAssignedCourse() {
		return assignedCourse;
	}

	public void setAssignedCourse(ArrayList<Course> assignedCourse) {
		this.assignedCourse = assignedCourse;
	}

	public void setTeachingContribution() {
		double baseContribution = 1, relaxation = 0.05;
		if (this.appt == 1){
			// Group 1 = Lecturer, research sci/fellow
			// Group 2 = Asst,Assoc,Prof
			baseContribution = 1.5;
		}
		
		if (adminAppt == 1){
			relaxation += 0.3;
		}
		else if (this.adminAppt == 2){
			relaxation += 0.5;
		}
		else if (this.adminAppt == 3){
			relaxation += 1;
		}
		
		if (this.serviceHr > 300){
			relaxation += 0.2;
		}
		else if (this.serviceHr > 200){
			relaxation += 0.1;
		}
		
		this.teachingContribution = baseContribution - relaxation;
		if (this.teachingContribution < 0){
			this.teachingContribution  = 0;
		}
	}
	
	public boolean isTeachable(String target){
		for(Course child : this.teachingCourses){
			if (child !=null){
				if (child.getCourseNum() == target ){
					return true;
				}
			}
		
		}
		return false;
	}
	
	public boolean isTeachableTut(String target){
		for(Course child : this.tutCourses){
			if (child !=null){
				if (child.getCourseNum() == target){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isTeachableLab(String target){
		for(Course child : this.labCourses){
			if (child !=null){
				if (child.getCourseNum() == target){
					return true;
				}
			}
		}
		return false;
	}
	
	public double getAllocatedWLU() {
		return allocatedWLU;
	}

	public void setAllocatedWLU(double allocatedWLU) {
		this.allocatedWLU = allocatedWLU;
	}
	
	public void addWLU(double wlu) {
		this.allocatedWLU += wlu;
	}
	
	public void resetWLUAllocated(){
		this.allocatedWLU = 0;
	}
	
	public String toString(){
		String result = this.id + "\t" + this.name +"\t"+ listOfCourse() + "\t\t\t" + teachingContribution;
		return result;
	}

	public String listOfCourse(){
		String result = "[";
		for (Course child : teachingCourses){
			if (child !=null){
				result += " " + child.getCourseNum();

			}
		}
	
		result +=" ] [";
		for (Course child : tutCourses){
			if (child !=null){
				result += " " + child.getCourseNum();
			}
		}
		
		result +=" ] [";
		for (Course child : labCourses){
			if (child !=null){
				result += " " + child.getCourseNum();
			}
		}
		
		result +=" ]";

		return result;
	}
	
	public String listOfCourse2(){
		String result = "[";
		for (Course child : assignedCourse){
			if (child !=null){
				result += " " + child.getCourseNum();

			}
		}
		result +=" ]";

		return result;
	}

	public boolean assignedContained(int target) {
		for(int i = 0; i < assignedCourse.size(); i++) {
			int compareID = assignedCourse.get(i).getId();
			if (compareID == target) {
				return false;
			}
		}
		return true;
	}
	
	public void resetAssignedContained(){
		assignedCourse = new ArrayList<Course>();
	}

	public String toString2(double avg) {
		// TODO Auto-generated method stub
		double optimal = teachingContribution * avg;
		double wluDis = Math.round((((this.allocatedWLU - optimal) / optimal) * 100) /10) * 10;
		return this.name + "\tTeaching Contribution: " +  this.teachingContribution + "\tAllocated WLU: " + this.allocatedWLU + "\tWLU DIST: " + wluDis + "\t\t" + listOfCourse2();
	}
	
	public String toString3(double avg) {
		// TODO Auto-generated method stub
		double optimal = teachingContribution * avg;
		double wluDis = Math.round((((this.allocatedWLU - optimal) / optimal) * 100) /10) * 10;
		return this.name + "\tWLU DIST: " + wluDis;
	}
	
	public String toString4() {
		return this.name + "\t " + countDaysOfWork();
	}
	
	public int getWLUVariation(double avg) {
		double optimal = teachingContribution * avg;
		double wluDis = Math.round((((this.allocatedWLU - optimal) / optimal) * 100) /10) * 10;

		int result = (int) wluDis;
		return result;
	}


}
