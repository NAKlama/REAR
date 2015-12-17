package de.uni.goettingen.REARClient.Net.SSH;

import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SSHconnection {
	private String		host;
	private String		user;
	private SSHkey		key;
	private JSch 		jsch;
	private Session 	ssh;
	private int			dataPort;
	
	public SSHconnection(String h, String u, SSHkey k, int dPort) {
		host		= h;
		user		= u;
		dataPort	= dPort;
		
		jsch = new JSch();
		key  = k;
		try {
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			jsch.addIdentity(key.getPrivKey().getAbsolutePath(), SSHkey.PASSPHRASE);
			System.out.println("Creating SSH session to "+user+"@"+host);
			ssh = jsch.getSession(user, host);
			ssh.setConfig(config);
			System.out.println("Initialized SSH");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void connect() {
		try {
//			ssh.connect();
			System.out.println("Forwarding port "+dataPort+":"+host+":"+dataPort+"\n");
			ssh.setPortForwardingL(dataPort, host, dataPort);
			ssh.connect();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		ssh.disconnect();
	}
	
		
	public void ensureConnection() {
		if(!ssh.isConnected()) {
			connect();
		}
	}
	
	public Boolean isConnected() {
		return ssh.isConnected();
	}
}
