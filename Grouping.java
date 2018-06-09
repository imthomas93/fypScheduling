package fypScheduling;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class Grouping implements Serializable{

	private static int incID =0; 

	private int id;
	private String groupName;
	private int size;
	private ArrayList<Course> courses;
	private int year;
	private int specialisation;
	private int[] dayOfWork;

	
	public Grouping(String groupName, int size, int year, int specialisation){
		id = incID++;
		this.groupName = groupName;
		this.size = size;
		courses = new ArrayList<Course>();
		this.setYear(year);
		this.setSpecialisation(specialisation);
		dayOfWork = new int[5];
		resetDayOfWork();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public ArrayList<Course> getCourses() {
		return courses;
	}
	public void setCourses(ArrayList<Course> course) {
		this.courses = course;
	
	}
	
	public void addCourse(Course target){
		this.courses.add(target);
	}
	
	public String toString(){
		String result = this.groupName +"\t"+ this.size +"\t"+ listOfCourse();
		return result;
	}
	
	public String listOfCourse(){
		String result = "";
		for (Course child : courses){
			result += " " + child.getCourseNum();
		}
		result = "[" + result + " ]";
		return result;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public boolean isSeniorYear() {
		if (this.year == 3 || this.year == 4){
			return true;
		}
		else{
			return false;
		}
	}

	public int getSpecialisation() {
		return specialisation;
	}

	public void setSpecialisation(int specialisation) {
		this.specialisation = specialisation;
	}

	public String toString2() {
		String result = this.groupName +"\t" + countDaysOfWork();
		return result;
	}
	
	public void resetDayOfWork() {
		for (int i = 0; i <5; i++) {
			dayOfWork[i] = 0;
		}
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
	
}
