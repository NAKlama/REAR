package de.uni.goettingen.REARController.Net;

import java.net.InetAddress;
import java.util.Stack;

import de.uni.goettingen.REARController.DataStruct.ClientStatus;

public class NetConnSignal {
	private ClientStatus	status;
	private String			recTime;
	private InetAddress		ip;
	private Boolean			connected;
	
	private Thread			conThread;
	
	private Stack<String>	commands;
	
	private String			pubKey;
	private String			id;
	private String			eid;
	private String[]		server;
	private String			playFile;
	private String			fileSize;
	
	private Object			commandLock		= new Object();
	private Object			connectedLock	= new Object();
	private Object			recTimeLock		= new Object();
	private Object			statusLock		= new Object();
	private Object			ipLock			= new Object();
	private Object			pubKeyLock		= new Object();
	private Object			idLock			= new Object();
	private Object			eidLock			= new Object();
	private Object			serverLock		= new Object();
	private Object			playFileLock	= new Object();
	private Object			sleepLock		= new Object();
	private Object			fSizeLock		= new Object();

	
	NetConnSignal(InetAddress ip_in) {
		ip			= ip_in;
		pubKey		= null;
		id			= null;
		eid			= null;
		connected	= false;
		fileSize    = null;
		server		= new String[2];
		commands	= new Stack<String>();
		conThread	= new Thread(new ClientConn(this));
		conThread.start();
	}
	
	public Object getSleepLock() {
		return sleepLock;
	}
	
	public String popCommand() {
		synchronized(commandLock) {
			if(!commands.empty()) {
				return commands.pop();
			}
		}
		return null;
	}
	
	public InetAddress getIP() {
		synchronized(ipLock) {
			return ip;
		}
	}
	
	public Boolean hasCommands() {
		synchronized(commandLock) {
			return ! commands.empty();
		}
	}
	
	public void setStatus(ClientStatus s) {
		synchronized(statusLock) {
			status = new ClientStatus(s);
		}
	}
	
	public ClientStatus getStatus() {
		synchronized(statusLock) {
			return status;
		}
	}
	
	public void setPubKey(String pk) {
		synchronized(pubKeyLock) {
			pubKey = new String(pk);
		}
	}
	
	public String getPubKey() {
		synchronized(pubKeyLock) {
			return pubKey;
		}
	}
	
	public String getID() {
		synchronized(idLock) {
			return id;
		}
	}
	
	public void setID(String i) {
		synchronized(idLock) {
			id = new String(i);
		}
		synchronized(commandLock) {
			commands.push("ID");
		}
	}
	
	public String getFileSize() {
		synchronized(fSizeLock) {
			return fileSize;
		}
	}
	
	public void setFileSize(String fSize) {
		synchronized(fSizeLock) {
			fileSize = new String(fSize);
		}
	}
	
	public String getExamID() {
		synchronized(eidLock) {
			return eid;
		}
	}
	
	public void setExamID(String i) {
		synchronized(eidLock) {
			eid = new String(i);
		}
		synchronized(commandLock) {
			commands.push("EID");
		}
	}
	
	public Boolean getConnected() {
		synchronized(connectedLock) {
			return connected;
		}
	}
	
	public Boolean isReachable() {
		synchronized(connectedLock) {
			return connected;
		}
	}
	
	public void setConnected(Boolean c) {
		synchronized(connectedLock) {
			connected = c;
		}
	}
	
	public void setServer(String s, String u) {
		synchronized(serverLock) {
			server[0] = new String(s);
			server[1] = new String(u);
		}
		synchronized(commandLock) {
			commands.push("SetServer");
		}
	}
	
	public String[] getServer() {
		synchronized(serverLock) {
			return server;
		}
	}
	
	public void setTime(String t) {
		synchronized(recTimeLock) {
			recTime = new String(t);
		}
	}
	
	public String getTime() {
		synchronized(recTimeLock) {
			return recTime;
		}
	}
	
	public void setPlayFile(String URL) {
		synchronized(playFileLock) {
			playFile = new String(URL);
		}
		synchronized(commandLock) {
			commands.push("playFile");
		}
	}
	
	public String getPlayFile() {
		synchronized(playFileLock) {
			return playFile;
		}
	}
	
	public void setPlayOnly() {
		synchronized(commandLock) {
			commands.push("playOnly");
		}
	}
	
	public void micRetry() {
		synchronized(commandLock) {
			commands.push("micRetry");
		}
	}
	
	public void init() {
		synchronized(commandLock) {
			commands.push("init");
		}
	}
	
	public void rec() {
		synchronized(commandLock) {
			commands.push("rec");
		}
	}
	
	public void stop() {
		synchronized(commandLock) {
			commands.push("stop");
		}
	}
	
	public void reset() {
		synchronized(commandLock) {
			commands.push("reset");
		}
	}
}
