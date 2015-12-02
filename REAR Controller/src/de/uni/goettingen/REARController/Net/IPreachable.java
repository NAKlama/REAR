package de.uni.goettingen.REARController.Net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

public class IPreachable implements Serializable {

	private static final long serialVersionUID = 510733392283538305L;
	private InetAddress ip;
	private Boolean		reachable;
	private Date		lastPing;
	private Boolean		threadRunning;
	
	public IPreachable(InetAddress i) {
		lastPing		= null;
		reachable		= false;
		threadRunning	= false;
		ip = i;
		initPingThread();
	}
	
	public InetAddress getAddress() {
		return ip;
	}
	
	public boolean isReachable() {
		if(lastPing != null && new Date().getTime() - lastPing.getTime() > 300000)
			initPingThread();
		return reachable;
	}
	
	private void initPingThread() {
		if(!threadRunning) {
			threadRunning = true;
			Thread t = new Thread(new PingThread(this));
			t.start();
		}
	}
	
	class PingThread implements Runnable {
		private IPreachable ipr;
		
		public PingThread(IPreachable i) {
			ipr = i;
		}

		@Override
		public void run() {
			try {
				reachable = ipr.ip.isReachable(500);
				ipr.lastPing = new Date();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ipr.threadRunning = false;
		}
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		reachable = false;
		out.defaultWriteObject();
	}
}
