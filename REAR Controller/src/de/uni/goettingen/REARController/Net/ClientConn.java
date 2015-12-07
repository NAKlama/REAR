package de.uni.goettingen.REARController.Net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import de.uni.goettingen.REARController.DataStruct.ClientStatus;

public class ClientConn {
	private InetAddress			ip;
	private Socket				sock;
	private DataOutputStream	out;
	private BufferedReader		in;
	private AuthToken			token;
	private String				salt;
	
	public ClientConn(InetAddress i) {
		ip = i;
		token = new AuthToken();
		try {
			sock	= new Socket(ip, 15000);
			out		= new DataOutputStream(sock.getOutputStream());
			in		= new BufferedReader(new InputStreamReader(sock.getInputStream()));
			sock.setKeepAlive(true);
			salt	= getSalt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ClientStatus status() {
		String reply = getReply("STATUS\n");
		int st = Integer.parseInt(reply);
		switch(st) {
		case 0:
			return new ClientStatus(true,  false, false, false, false);
		case 1:
			return new ClientStatus(false, true,  false, false, false);
		case 2:
			return new ClientStatus(false, false, true,  false, false);
		case 3:
			return new ClientStatus(false, false, false, true,  false);
		case 4:
			return new ClientStatus(false, false, false, false, true );
		}
		return null;		
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public Boolean isReachable() {
		return sock.isConnected();
	}
	
	private String getSalt() {
		return getReply("GETSALT\n").trim();
	}
	
	public String getPubKey() {
		return getReply("GETPUBKEY\n").trim();
	}
	
	public String getID() {
		return getReply("ID\n").trim();
	}
	
	public boolean setID(String id) {
		String reply = getReply("ID " + id + "\n").trim();
		if(reply.equals("OK"))
			return true;
		return false;
	}
	
	public boolean setExamID(String eID) {
		String reply = getReply("EXAMID " + eID + "\n").trim();
		if(reply.equals("OK"))
			return true;
		return false;		
	}
	
	public String getTime() {
		return getReply("RECTIME\n").trim();
	}
	
	public boolean init() {
		return sendAuthCommand("INIT");
	}
	
	public boolean rec() {
		return sendAuthCommand("REC");
	}
	
	public boolean stop() {
		return sendAuthCommand("STOP");
	}
	
	public boolean reset() {
		return sendAuthCommand("RESET");
	}
		
	private String getReply(String c) {
		try {
			System.out.println("> " + c.trim());
			out.writeBytes(c);
			String reply = in.readLine();
			System.out.println("< " + reply);
			return reply;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean sendAuthCommand(String c) {
		try {
			String command = c.trim() + " ";
			System.out.println("> " + command + "[TOKEN]");
			command += token.getToken(c, salt) + "\n";			
			out.writeBytes(command);
			String reply = in.readLine();
			System.out.println("< " + reply);
			if(reply.trim().equals("OK"))
				return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
