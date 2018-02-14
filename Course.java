package fypScheduling;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;


public class Course implements Serializable{

	private static int incID =0; 
	
	private int id;
	private String courseNum;
	private String courseName;
	private int noOfLecs;
	private int noOfTuts;
	private int noOfLabs;
	
	public Course(String courseNum, String courseName, int noOfLecs, int noOfTuts, int noOfLabs){
		id = incID++;
		this.courseNum = courseNum;
		this.courseName = courseName;
		this.noOfLecs = noOfLecs;
		this.noOfTuts = noOfTuts;
		this.noOfLabs = noOfLabs;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getCourseNum() {
		return courseNum;
	}
	public void setCourseNum(String courseNum) {
		this.courseNum = courseNum;
	}
	public int getNoOfLecs() {
		return noOfLecs;
	}
	public void setNoOfLecs(int noOfLecs) {
		this.noOfLecs = noOfLecs;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public int getNoOfTuts() {
		return noOfTuts;
	}
	public void setNoOfTuts(int noOfTuts) {
		this.noOfTuts = noOfTuts;
	}
	public int getNoOfLabs() {
		return noOfLabs;
	}
	public void setNoOfLabs(int noOfLabs) {
		this.noOfLabs = noOfLabs;
	}
	
	public String toString(){
		String result = this.id + "\t" + this.courseNum +"\t"+ this.courseName +"\t"+ this.noOfLecs +"\t"+ this.noOfTuts +"\t"+ this.noOfLabs;
		return result;
	}



}
