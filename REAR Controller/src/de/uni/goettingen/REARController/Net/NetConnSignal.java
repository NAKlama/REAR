package de.uni.goettingen.REARController.Net;

import java.net.InetAddress;
import java.util.Stack;

import de.uni.goettingen.REARController.DataStruct.ClientStatus;

public class NetConnSignal {
	private ClientStatus	status;
	private String			recTime;
	private IPreachable		ipr;
	private Boolean			connected;
	
	private Thread			conThread;
	
	private Stack<String>	commands;
	
	private String			pubKey;
	private String			id;
	private String			eid;
	private String[]		server;
	private String			playFile;

	
	NetConnSignal(IPreachable ip) {
		ipr			= ip;
		pubKey		= null;
		id			= null;
		connected	= false;
		server		= new String[2];
		commands	= new Stack<String>();
		conThread	= new Thread(new ClientConn(this));
		conThread.start();
	}
	
	public synchronized String popCommand() {
		if(!commands.empty()) {
			return commands.pop();
		}
		return null;
	}
	
	public synchronized IPreachable getIPR() {
		return ipr;
	}
	
	public synchronized Boolean hasCommands() {
		return ! commands.empty();
	}
	
	public synchronized void setStatus(ClientStatus s) {
		status = new ClientStatus(s);
	}
	
	public synchronized ClientStatus getStatus() {
		return status;
	}
	
	public synchronized void setPubKey(String pk) {
		pubKey = new String(pk);
	}
	
	public synchronized String getPubKey() {
		return pubKey;
	}
	
	public synchronized InetAddress getIP() {
		return ipr.getAddress();
	}
	
	public synchronized String getID() {
		return id;
	}
	
	public synchronized void setID(String i) {
		id = new String(i);
		commands.push("ID");
		this.notifyAll();
	}
	
	public synchronized String getExamID() {
		return eid;
	}
	
	public synchronized void setExamID(String i) {
		eid = new String(i);
		commands.push("EID");
		this.notifyAll();
	}
	
	public synchronized Boolean getConnected() {
		return connected;
	}
	
	public synchronized Boolean isReachable() {
		return connected;
	}
	
	public synchronized void setConnected(Boolean c) {
		connected = c;
	}
	
	public synchronized void setServer(String s, String u) {
		server[0] = new String(s);
		server[1] = new String(u);
		commands.push("SetServer");
	}
	
	public synchronized String[] getServer() {	
		return server;
	}
	
	public synchronized void setTime(String t) {
		recTime = new String(t);
	}
	
	public synchronized String getTime() {
		return recTime;
	}
	
	public synchronized void setPlayFile(String URL) {
		playFile = new String(URL);
		commands.push("playFile");
	}
	
	public synchronized String getPlayFile() {
		return playFile;
	}
	
	public synchronized void init() {
		commands.push("init");
	}
	
	public synchronized void rec() {
		commands.push("rec");
	}
	
	public synchronized void stop() {
		commands.push("stop");
	}
	
	public synchronized void reset() {
		commands.push("reset");
	}
}
