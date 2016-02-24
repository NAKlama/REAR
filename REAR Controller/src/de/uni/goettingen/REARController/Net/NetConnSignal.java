package de.uni.goettingen.REARController.Net;

import java.net.InetAddress;
import java.util.Stack;

import de.uni.goettingen.REARController.DataStruct.ClientStatus;

public class NetConnSignal {
	private ClientStatus	status;
	private String			recTime;
	private IPreachable		ipr;
	private Boolean			connected;
	
	@SuppressWarnings("unused")
	private Thread			conThread;
	
	private Stack<String>	commands;
	
	private String			pubKey;
	private String			id;
	private String			eid;
	private String[]		server;

	
	NetConnSignal(IPreachable ip) {
		ipr			= ip;
		pubKey		= null;
		id			= null;
		connected	= null;
		server		= new String[2];
		commands	= new Stack<String>();
		conThread	= new Thread(new ClientConn(this));
	}
	
	public String popCommand() {
		synchronized(this) {
			if(!commands.empty()) {
				return commands.pop();
			}
			return null;
		}
	}
	
	public IPreachable getIPR() {
		return ipr;
	}
	
	public Boolean hasCommands() {
		return ! commands.empty();
	}
	
	public void setStatus(ClientStatus s) {
		status = s;
	}
	
	public ClientStatus getStatus() {
		return status;
	}
	
	public void setPubKey(String pk) {
		pubKey = pk;
	}
	
	public String getPubKey() {
		return pubKey;
	}
	
	public InetAddress getIP() {
		return ipr.getAddress();
	}
	
	public String getID() {
		return id;
	}
	
	public void setID(String i) {
		id = i;
		commands.push("ID");
		this.notifyAll();
	}
	
	public String getExamID() {
		return eid;
	}
	
	public void setExamID(String i) {
		eid = i;
		commands.push("EID");
		this.notifyAll();
	}
	
	public Boolean getConnected() {
		return connected;
	}
	
	public Boolean isReachable() {
		return connected;
	}
	
	public void setConnected(Boolean c) {
		connected = c;
	}
	
	public void setServer(String s, String u) {
		server[0] = s;
		server[1] = u;
		commands.push("SetServer");
	}
	
	public String[] getServer() {	
		return server;
	}
	
	public void setTime(String t) {
		recTime = t;
	}
	
	public String getTime() {
		return recTime;
	}
	
	public void init() {
		commands.push("init");
	}
	
	public void rec() {
		commands.push("rec");
	}
	
	public void stop() {
		commands.push("stop");
	}
	
	public void reset() {
		commands.push("reset");
	}
}
