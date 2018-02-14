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
	
	public Grouping(String groupName, int size, int year){
		id = incID++;
		this.groupName = groupName;
		this.size = size;
		courses = new ArrayList<Course>();
		this.setYear(year);
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
	
}
