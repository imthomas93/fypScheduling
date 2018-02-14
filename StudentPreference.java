package fypScheduling;

import java.util.ArrayList;

public class StudentPreference {

	private Course course;
	private ArrayList<Ranking> rankList;
	
	public StudentPreference(Course course) {
		this.course = course;
		this.rankList = new ArrayList<Ranking>();
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
	public void setRankList(ArrayList<Ranking> rankList) {
		this.rankList = rankList;
	}
	
	public boolean containProf(int facID) {
		for (Ranking child : rankList) {
			if (child.getFacID() == facID) {
				return true;
			}
		}
		return false;
	}
	
	public int getPreferenceScore(int facID) {
		int score = 6;
		for (Ranking child : rankList) {
			if (child.getFacID() == facID) {
				score = child.getRank();
			}
				
		}
		return score;
	}
	
	
}
