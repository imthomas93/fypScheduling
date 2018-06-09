package fypScheduling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Cloneable, Serializable{
	public static enum Type { LEC, TUT, LAB };
	
	private static int incID = 1;
	
	private int id;
	private Type type;
	private int eventGroupId;
	private int size;
	private ArrayList<Faculty> faculty;
	private Course course;
	private Grouping grouping;
	private int eventDuration;
	private double WLU;

	
	public Event (Type type, int size, ArrayList<Faculty> faculty, Course course, Grouping grouping, int eventGroupId, int eventDuration){
		this.type = type;
		this.size = size;
		this.faculty = faculty;
		this.course = course;
		this.grouping = grouping;
		this.id = incID++;
		this.eventGroupId = eventGroupId;
		this.eventDuration = eventDuration;
		this.WLU = calculateWLU();
	}

	public Event (int id, Type type, int eventGroupID, int size, ArrayList<Faculty> faculty, Course course, Grouping grouping, int eventDuration, double wlu){
		this.id = id;
		this.type = type;
		this.eventGroupId = eventGroupID;
		this.size = size;
		this.faculty = faculty;
		this.course = course;
		this.grouping = grouping;
		this.eventDuration = eventDuration;
		this.WLU = wlu;
	}
	
	@Override
    protected Event clone() {
        Event clone = null;
        try{
            clone = (Event) super.clone();
        }catch(CloneNotSupportedException e){
            throw new RuntimeException(e); // won't happen
        }
        return clone;
    }
	
	public Event deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (Event) ois.readObject();
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	@SuppressWarnings("null")
	public Event copy() {
		Event newEvent = null;
		newEvent.id = this.id;
		newEvent.type = this.type;
		newEvent.faculty = this.faculty;
		newEvent.course = this.course;
		newEvent.grouping = this.grouping;
		newEvent.eventDuration = this.eventDuration;
		newEvent.WLU = this.WLU;
		return newEvent;
	}
	
	public Event(Event event) {
		this.id = event.id;
		this.type = event.type;
		this.size = event.size;
		this.faculty = event.faculty;
		this.course = event.course;
		this.grouping = event.grouping;
		this.eventGroupId = event.eventGroupId;
		this.eventDuration = event.eventDuration;
		this.WLU = calculateWLU();
	}
	public void updateWLU(double novelty){
		this.WLU *=novelty;
	}
	
	public double getWLU(){
		return this.WLU;
	}
	
	private double calculateWLU() {
		double result;
		double parameter;
		if(type == Type.LEC){
			if (size > 300){
				parameter = 2;
			}
			else if (size > 150){
				parameter = 1.75;
			}
			else if(size > 60){
				parameter = 1.5;
			}
			else{
				parameter = 1;
			}
			result = 2.5 * parameter * 13 * eventDuration;
		}
		else if(type == Type.TUT){
			result = 1.5 * 13 * eventDuration;
		}
		else{
			if (this.eventDuration == 1){
				result = 1.4 * 13 * eventDuration * 1.25;
			}
			else{
				result = 1.4 * 5 * eventDuration * 1.25;
			}
			
		}
		return result;
	}

	
	public void calculateWLU(double novelity) {
		double result;
		double parameter;
		if(type == Type.LEC){
			if (size > 300){
				parameter = 2;
			}
			else if (size > 150){
				parameter = 1.75;
			}
			else if(size > 60){
				parameter = 1.5;
			}
			else{
				parameter = 1;
			}
			result = 2.5 * parameter * 13 * eventDuration * novelity;
		}
		else if(type == Type.TUT){
			result = 1.5 * 13 * eventDuration * novelity * 1.25;
		}
		else{
			if (this.eventDuration == 1){
				result = 1.4 * 13 * eventDuration * novelity * 1.25;
			}
			else{
				result = 1.4 * 5 * eventDuration * novelity * 1.25;
			}
			
		}
		this.WLU = result;
	}

	public int getId() {
		return id;
	}

	public int getEventGroupId() {
		return eventGroupId;
	}

	public int getSize() {
		return size;
	}

	public ArrayList<Faculty> getFaculty() {
		return faculty;
	}

	public Course getCourse() {
		return course;
	}

	public Grouping getGrouping() {
		return grouping;
	}
	
	public int getEventDuration() {
		return eventDuration;
	}
	
	public static Type setType(int i){
		if (i == 1){
			return Type.LEC;
		}
		else if(i == 2){
			return Type.TUT;
		}
		else if (i == 3){
			return Type.LAB;
		}
		return null;
	}

	public Type getType() {
		return type;
	}
	
	public void setSize(int newSize) {
		this.size = newSize;
	}
	

	
	public String toString(){
		String subOut= "[ ";
		for (Faculty child : faculty){
			subOut += child.getName() + " ";
		}
		subOut += "]";
	
		return eventGroupId + "\t" +  grouping.getGroupName() + "\t" + course.getCourseNum() + "\t" +  type + "\t" +  size + "\t" + subOut + "\t\t\t" + WLU + "\t";
	}
	
	public void swapFaculty(int i, Faculty newFac) {
		// TODO Auto-generated method stub
		faculty.remove(i);
		faculty.add(newFac);
	}

	public boolean doesNotCrash(int target) {
		for(Faculty child : faculty) {
			if(child.getId() == target) {
				return true;
			}
		}
		return false;
	}
	
}
