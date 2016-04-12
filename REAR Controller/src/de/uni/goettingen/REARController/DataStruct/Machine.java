package de.uni.goettingen.REARController.DataStruct;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import de.uni.goettingen.REARController.GUI.Tools.IDfactory;

public class Machine {
	private long			id;
	private String			computerID;
	private AreaTreeNode	aNode;
	private Area			area;
	private InetAddress		ip;
	private String			studentID;
	private ClientStatus	status;
	private String			recTime;
	private Boolean			delete;
	
	public Machine(Machine m) {
		computerID	= m.getComputerID();
		area		= m.getArea();
		ip			= m.getIP();
		studentID	= m.getStudentID();
		status		= m.getClientStatus();
		recTime		= m.getRecTime();
		delete		= false;
		id			= new IDfactory().getID();
	}
	
	public Machine(String cid, Area a, InetAddress i, String studID, ClientStatus s, String rTime, long idIn) {
		computerID	= cid;
		area		= a;
		ip			= i;
		studentID	= studID;
		status		= s;
		recTime		= rTime;
		delete		= false;
		id			= idIn;
	}
	
	public Machine(String cid, Area a, InetAddress i, String studID, ClientStatus s, String rTime) {
		computerID	= cid;
		area		= a;
		ip			= i;
		studentID	= studID;
		status		= s;
		recTime		= rTime;
		delete		= false;
		id			= new IDfactory().getID();
	}
	
	public Machine(String cid, Area a, InetAddress i) {
		computerID	= cid;
		area		= a;
		ip			= i;
		studentID	= "";
		status		= new ClientStatus(StatusEnum.UNINITIALIZED);
		recTime		= "0:00:00";
		delete		= false;
		id			= new IDfactory().getID();
	}
	
	public Machine(String cid, Area a) {
		computerID	= cid;
		area		= a;
		ip			= null;
		studentID	= "";
		status		= new ClientStatus(StatusEnum.UNINITIALIZED);
		recTime		= "0:00:00";
		delete		= false;
		id			= new IDfactory().getID();
	}
	
	public Machine() {
		computerID	= "";
		area		= null;
		ip			= null;
		studentID	= "";
		status		= new ClientStatus(StatusEnum.UNINITIALIZED);
		recTime		= "0:00:00";
		delete		= false;
		id			= new IDfactory().getID();
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
	
	public void setStatus(ClientStatus s) {
		status = s;
	}
	
	public void setStatus(StatusEnum s) {
		status = new ClientStatus(s);
	}
	
	public void setRecTime(String t) {
		recTime = t;
	}
	
	public void setDelete() {
		delete = true;
	}
	
	public void setDelete(Boolean d) {
		delete = d;
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
			status = (ClientStatus) o;
			break;
		case 5:
			recTime = (String) o;
			break;
		case 6:
			delete = (Boolean) o;
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
	
	public String getIPstr() {
		return ip.toString();
	}
	
	public String getStudentID() {
		return studentID;
	}
	
	public StatusEnum getStatus() {
		return status.getStatus();
	}
	
	public ClientStatus getClientStatus() {
		return status;
	}
	
	public String getRecTime() {
		return recTime;
	}
	
	public Boolean getDelete() {
		return delete;
	}
	
	public long getID() {
		return id;
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
		case 6:
			return delete;
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
		v.addElement(delete);
		v.addElement(id);
		return v;
	}
}
