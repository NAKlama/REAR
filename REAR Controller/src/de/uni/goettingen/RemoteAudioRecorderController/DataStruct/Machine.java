package de.uni.goettingen.RemoteAudioRecorderController.DataStruct;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Machine {
	private String			computerID;
	private Area			area;
	private InetAddress		ip;
	private String			studentID;
	private int				status;
	private String			recTime;
	
	public Machine(Machine m) {
		computerID	= m.getComputerID();
		area		= m.getArea();
		ip			= m.getIP();
		studentID	= m.getStudentID();
		status		= m.getStatus();
		recTime		= m.getRecTime();
	}
	
	public Machine(String id, Area a, InetAddress i) {
		computerID	= id;
		area		= a;
		ip			= i;
		studentID	= "";
		status		= 0;
		recTime		= "";
	}
	
	public Machine(String id, Area a) {
		computerID	= id;
		area		= a;
		ip			= null;
		studentID	= "";
		status		= 0;
		recTime		= "";
	}
	
	public Machine() {
		computerID	= "";
		area		= null;
		ip			= null;
		studentID	= "";
		status		= 0;
		recTime		= "";
	}
	
	public void setComputerID(String id) {
		computerID = id;
	}
	
	public void setArea(Area a) {
		area = a;
	}
	
	public void setIP(InetAddress i) {
		ip = i;
	}
	
	public void setStudentID(String sid) {
		studentID = sid;
	}
	
	public void setStatus(int s) {
		status = s;
	}
	
	public void setRecTime(String t) {
		recTime = t;
	}
	
	public void setValue(int i, Object o) {
		switch(i) {
		case 0:
			computerID = (String) o;
			break;
		case 1:
			area = (Area) o;
			break;
		case 2:
			try {
				ip = InetAddress.getByName((String) o);
			} catch (UnknownHostException e) {
				ip = null;
			}
			break;
		case 3:
			studentID = (String) o;
			break;
		case 4:
			status = (int) o;
			break;
		case 5:
			recTime = (String) o;
			break;
		}
	}
	
	public String getComputerID() {
		return computerID;
	}
	
	public Area getArea() {
		return area;
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public String getStudentID() {
		return studentID;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getRecTime() {
		return recTime;
	}
	
	public Object getValue(int i) {
		switch(i) {
		case 0:
			return computerID;
		case 1:
			return area.getName();
		case 2:
			return ip.getHostAddress();
		case 3:
			return studentID;
		case 4:
			return status;
		case 5:
			return recTime;
		}
		return null;
	}
}
