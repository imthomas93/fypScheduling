package fypScheduling;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class Venue implements Serializable{
	private static int incID =0; 
	
	private int id;
	private String roomName;
	private int capacity;
	private Event.Type type;
	
	public Venue(String roomName, int capacity, Event.Type type){
		id = incID++;
		this.roomName = roomName;
		this.capacity = capacity;
		this.type = type;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public Event.Type getType() {
		return type;
	}
	public void setType(Event.Type type) {
		this.type = type;
	}
	
	public String toString(){
		String result = this.roomName +"\t"+ this.capacity +"\t"+ this.type;
		return result;
	}
	
	
}
