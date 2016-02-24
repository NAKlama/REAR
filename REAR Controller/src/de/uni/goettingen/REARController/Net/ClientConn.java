package de.uni.goettingen.REARController.Net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

import de.uni.goettingen.REARController.DataStruct.ClientStatus;

public class ClientConn implements Runnable {
	private Boolean				connect;
	private IPreachable			ip;
	private Socket				sock;
	private DataOutputStream	out;
	private BufferedReader		in;
	private AuthToken			token;
	private String				salt;
	private Date				connectCheckTime;
	private NetConnSignal		sig;
	
	private Boolean				loop = true;

	public ClientConn(NetConnSignal s) {
		connectCheckTime = null;
		connect = false;
		sig = s;
		ip  = sig.getIPR();
		token = new AuthToken();
		if(this.checkConnection()) {
			sig.setPubKey(this.getPubKey());
		}
	}

	private Boolean checkConnection() {
		if(connect)
			return true;
		if(connectCheckTime == null || (new Date()).getTime() - connectCheckTime.getTime() > (60 * 1000)) {
			connectCheckTime = new Date();
			try {
				sock	= new Socket(ip.getAddress(), 15000);
				out		= new DataOutputStream(sock.getOutputStream());
				in		= new BufferedReader(new InputStreamReader(sock.getInputStream()));
				sock.setKeepAlive(true);
				connect = true;
				ip.setReachable(true);
				salt	= getSalt();
			} catch (ConnectException e) {
				connect = false;
				ip.setReachable(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return connect;
	}

	public ClientStatus status() {
		if(checkConnection()) {
			String reply = getReply("STATUS\n");
			//		System.out.println("Status reply: " + reply);
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
			return new ClientStatus(true,  false, false, false, false);
		}
		return null;		
	}

	public InetAddress getIP() {
		return ip.getAddress();
	}

	public Boolean isReachable() {
		return connect && sock.isConnected();
	}

	private String getSalt() {
		if(checkConnection())
			return getReply("GETSALT\n").trim();
		return null;
	}

	public String getPubKey() {
		if(checkConnection())
			return getReply("GETPUBKEY\n").trim();
		return null;
	}

	public String getID() {
		if(checkConnection())
			return getReply("ID\n").trim();
		return null;
	}
	

	public String getExamID() {
		if(checkConnection())
			return getReply("EXAMID\n").trim();
		return null;
	}


	public boolean setID(String id) {
		if(checkConnection()) {
			String sendID = id.replaceAll("\\s", "_");
			String replyID = "";
			while(!replyID.equals(id)) {
				String reply = getReply("ID " + sendID + "\n").trim();
				if(reply.equals("OK")) {
					replyID = this.getID();
				}
			}
		}
		return false;
	}

	public boolean setExamID(String eID) {
		if(checkConnection()) {
			String replyID = "";
			while(!replyID.equals(eID)) {
				String reply = getReply("EXAMID " + eID + "\n").trim();
				if(reply.equals("OK"))
					replyID = this.getExamID();
			}
		}
		return false;		
	}

	public String getSSHkey() {
		if(checkConnection())
			return getReply("GETPUBKEY\n").trim();
		return null;
	}

	public String getTime() {
		if(checkConnection())
			return getReply("RECTIME\n");
		return null;
	}

	public boolean init() {
		if(checkConnection())
			return sendAuthCommand("INIT");
		return false;
	}

	public boolean rec() {
		if(checkConnection())
			return sendAuthCommand("REC");
		return false;
	}

	public boolean stop() {
		if(checkConnection())
			return sendAuthCommand("STOP");
		return false;
	}

	public boolean reset() {
		if(checkConnection())
			return sendAuthCommand("RESET");
		return false;
	}

	public boolean setServer(String uploadServer, String uploadUser) {
		if(checkConnection()) {
			boolean a, b;
			a = sendAuthParCommand("UPLOADSERVER", uploadServer);
			b = sendAuthParCommand("UPLOADUSER", uploadUser);
			return a && b;
		}
		return false;
	}

	private String getReply(String c) {
		try {
			System.out.println("> " + c.trim());
			out.writeBytes(c);
			String reply = in.readLine();
			while(in.ready()) {
				in.readLine();
			}
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
			command += token.getToken(c.trim(), salt) + "\n";			
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

	private boolean sendAuthParCommand(String c, String par) {
		try {
			String command = c.trim() + " ";
			System.out.println("> " + command + "[TOKEN]");
			command += par + " ";
			command += token.getToken(c.trim(), salt) + "\n";			
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

	@Override
	public void run() {
		while(loop) {
			try {
				sig.wait(1000);
			} catch (InterruptedException e) {
			}
			if(this.isReachable()) {
				sig.setConnected(true);
				if(sig.hasCommands()) {
					if(sig.popCommand().equals("ID"))
						setID(sig.getID());
					if(sig.popCommand().equals("EID"))
						setExamID(sig.getExamID());
					if(sig.popCommand().equals("SetServer")) {
						String[] server = sig.getServer();
						this.setServer(server[0], server[1]);
					}
					if(sig.popCommand().equals("init"))
						this.init();
					if(sig.popCommand().equals("rec"));
						this.rec();
					if(sig.popCommand().equals("stop"));
						this.stop();
					if(sig.popCommand().equals("reset"));
						this.reset();
					if(sig.popCommand().equals("STOP_THREAD"))
						loop = false;
				}
				sig.setStatus(this.status());
				sig.setTime(this.getTime());
			} else {
				sig.setConnected(false);
			}
		}
	}
}
