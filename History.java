package fypScheduling;

import java.util.ArrayList;

public class History {
	
	private Course course;
	private ArrayList<Faculty> facList;
	
	public History(Course course) {
		this.setCourse(course);
		this.setFacList(new ArrayList<Faculty>());
	}

	public ArrayList<Faculty> getFacList() {
		return facList;
	}

	public void setFacList(ArrayList<Faculty> facList) {
		this.facList = facList;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}
	
	public String toString() {
		return course.getCourseNum() + "\t" + getFacName();
	}
	
	public void addFac(Faculty child) {
		this.facList.add(child);
	}

	private String getFacName() {
		String result = "[ ";
		for (Faculty child: facList) {
			result += child.getName() + " ";
		}
		result += "]";
		return result;
	}
	

	public Integer getId() {
		// TODO Auto-generated method stub
		return course.getId();
	}

	public double findFac(ArrayList<Faculty> target) {
		double defaultValue = 1.5;
		for (Faculty child: target) {
			for (Faculty facChild : this.facList) {
				if (child.getId() == facChild.getId()) {
					return 1;
				}
			}
		}
		return defaultValue;
	}
}
