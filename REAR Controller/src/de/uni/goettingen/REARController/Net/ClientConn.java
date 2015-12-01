package de.uni.goettingen.REARController.Net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import de.uni.goettingen.REARController.DataStruct.Machine;

public class ClientConn {
	private Machine				machine;
	private InetAddress			ip;
	private Socket				sock;
	private DataOutputStream	out;
	private BufferedReader		in;
	private AuthToken			token;
	private String				salt;
	
	public ClientConn(Machine m) {
		machine = m;
		ip = m.getIP();
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
	
	public Machine getMachine() {
		return machine;
	}
	
	public int status() {
		String reply = getReply("STATUS\n");
		if(reply != null)
			return Integer.parseInt(reply);
		return -1;		
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
			out.writeBytes(c);
			return in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean sendAuthCommand(String c) {
		try {
			String command = c.trim() + " ";
			command += token.getToken(c, salt) + "\n";			
			out.writeBytes(command);
			String reply = in.readLine();
			if(reply.trim().equals("OK"))
				return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	
}
