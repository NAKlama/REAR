package de.uni.goettingen.RemoteAudioRecorderController.DataStruct;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

public class Machine {
	private String			computerID;
	private AreaTreeNode	aNode;
	private Area			area;
	private InetAddress		ip;
	private String			studentID;
	private Status				status;
	private String			recTime;
	
	public Machine(Machine m) {
		computerID	= m.getComputerID();
		area		= m.getArea();
		ip			= m.getIP();
		studentID	= m.getStudentID();
		status		= m.getStatus();
		recTime		= m.getRecTime();
	}
	
	public Machine(String id, Area a, InetAddress i, String studID, Status s, String rTime) {
		computerID	= id;
		area		= a;
		ip			= i;
		studentID	= studID;
		status		= s;
		recTime		= rTime;
	}
	
	public Machine(String id, Area a, InetAddress i) {
		computerID	= id;
		area		= a;
		ip			= i;
		studentID	= "";
		status		= new Status(StatusEnum.UNINITIALIZED);
		recTime		= "0:00:00";
	}
	
	public Machine(String id, Area a) {
		computerID	= id;
		area		= a;
		ip			= null;
		studentID	= "";
		status		= new Status(StatusEnum.UNINITIALIZED);
		recTime		= "0:00:00";
	}
	
	public Machine() {
		computerID	= "";
		area		= null;
		ip			= null;
		studentID	= "";
		status		= new Status(StatusEnum.RECORDING);
		recTime		= "0:00:00";
	}
	
	public void setComputerID(String id) {
		computerID = id;
	}
	
	public void setAreaTreeNode(AreaTreeNode atn) {
		aNode = atn;
		area  = (Area) aNode.getUserObject();
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
	
	public void setStatus(Status s) {
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
			status = (Status) o;
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
	
	public AreaTreeNode getAreaTreeNode() {
		return aNode;
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public String getStudentID() {
		return studentID;
	}
	
	public Status getStatus() {
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
	
	public Vector<Object>	getObjVector() {
		Vector<Object> v = new Vector<Object>();
		v.addElement(computerID);
		v.addElement(aNode);
		v.addElement(ip);
		v.addElement(studentID);
		v.addElement(status);
		v.addElement(recTime);
		return v;
	}
}
