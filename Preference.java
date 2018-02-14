package fypScheduling;

import java.util.ArrayList;
import java.util.Collections;


public class Preference {

	private Course course;
	private ArrayList<Ranking> rankList;
	
	public Preference(Course course){
		this.course = course;
		rankList = new ArrayList<Ranking>();
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public ArrayList<Ranking> getRankList() {
		return rankList;
	}
	
	public int getRankListSize(){
		return rankList.size();
	}

	public void setRankList(ArrayList<Ranking> rankList) {
		this.rankList = rankList;
	}
	
	public void sortRank(){
		Collections.sort(rankList);
	}

	public String toString(){
		String subOut= "[ ";
		for (Ranking child : rankList){
			subOut += child.toString() + " ";
		}
		subOut += "]";
		String output = course.getCourseNum() + "\t" + subOut;
		
		return output;
	}
	
	public int findRank(int id){
		int result = 5;
		for (Ranking child : rankList){
			if(child.getFacID() == id){
				result = child.getRank();
			}
		}
		return result;
	}
}
