package de.uni.goettingen.REARController.Net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import de.uni.goettingen.REARController.DataStruct.ClientStatus;

public class ClientConn implements Runnable {
	private Boolean				connect;
	private InetAddress			ip;
	private Socket				sock;
	private DataOutputStream	out;
	private BufferedReader		in;
	private AuthToken			token;
	private String				salt;
	private NetConnSignal		sig;
	private String				modeString;
	private String				pubKey;
	
	private Boolean				loop = true;

	public ClientConn(NetConnSignal s) {
		modeString = "None";
		pubKey = null;
		connect = false;
		sig = s;
		ip  = sig.getIP();
		token = new AuthToken();
	}

	private Boolean checkConnection() {
		if(connect)
			return true;
		else {
			try {
				sock	= new Socket(ip, 15000);
				out		= new DataOutputStream(sock.getOutputStream());
				in		= new BufferedReader(new InputStreamReader(sock.getInputStream()));
				sock.setKeepAlive(true);
				connect = true;
				sig.setConnected(true);
				salt	= getSalt();
			} catch (ConnectException e) {
				connect = false;
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
			case -1: // Mic Error
				return new ClientStatus(false, true, false,  false, false, false, false, false);
			case 0:  // Uninitialized
				return new ClientStatus(true, false, false,  false, false, false, false, false);
			case 1:  // Initialized
				return new ClientStatus(false, false, true,  false, false, false, false, false);
			case 2:
				return new ClientStatus(false, false, false, false, false, false, true,  false);
			case 3:
				return new ClientStatus(false, false, false, false, false, false, false, true);
			case 4:
				return new ClientStatus(false, false, false, true,  false, false, false, false);
			case 5:
				return new ClientStatus(false, false, false, false, true,  false, false, false);
			case 6:
				return new ClientStatus(false, false, false, false, false, true , false, false);
			}
			return     new ClientStatus(true , false, false, false, false, false, false, false);
		}
		return new ClientStatus();		
	}

	public InetAddress getIP() {
		return ip;
	}
	
	public Boolean isReachable() {
		return connect && sock.isConnected();
	}

	private String getSalt() {
		if(checkConnection())
			return getReply("GETSALT\n").trim();
		return null;
	}

	private String getPubKey() {
		if(pubKey == null) {
			if(checkConnection())
				pubKey = getReply("GETPUBKEY\n").trim();
		}
		return pubKey;
	}

	private String getID() {
		if(checkConnection())
			return getReply("ID\n").trim();
		return null;
	}
	

	private String getExamID() {
		if(checkConnection())
			return getReply("EXAMID\n").trim();
		return null;
	}

	public String getFileSize() {
		if(checkConnection())
			return getReply("FILESIZE\n").trim();
		return null;
	}

	private boolean setID(String id) {
		if(checkConnection()) {
			String sendID = id.replaceAll("\\s", "_");
			String reply = getReply("ID " + sendID + "\n").trim();
			if(reply.equals("OK"))
				return true;	
		}
		return false;
	}

	private boolean setExamID(String eID) {
		if(checkConnection()) {
			String reply = getReply("EXAMID " + eID + "\n").trim();
			if(reply.equals("OK"))
				return true;
		}
		return false;		
	}
	
	private boolean setPlayFile(String URL) {
		if(checkConnection()) {
			return sendAuthCommandOpt("PLAYFILE", URL);
		}
		return false;
	}
	
	private boolean setPlayOnly() {
		if(checkConnection()) {
			return sendAuthCommand("PLAYONLY");
		}
		return false;
	}

	public String getSSHkey() {
		if(checkConnection())
			return getReply("GETPUBKEY\n").trim();
		return null;
	}

	private String getTime() {
		if(checkConnection())
			return getReply("RECTIME\n");
		return null;
	}
	
	private boolean micRetry() {
		if(checkConnection())
			return sendAuthCommand("MICRETRY");
		return false;
	}

	private boolean init() {
		if(checkConnection())
			return sendAuthCommand("INIT");
		return false;
	}

	private boolean rec() {
		if(checkConnection())
			return sendAuthCommand("REC");
		return false;
	}
	
	private boolean recTest(String recTestURL) {
		if(checkConnection())
			return sendAuthParCommand("AUDIOTEST", recTestURL);
		return false;
	}

	private boolean stop() {
		if(checkConnection())
			return sendAuthCommand("STOP");
		return false;
	}

	private boolean reset() {
		if(checkConnection())
			return sendAuthCommand("RESET");
		return false;
	}

	private boolean setServer(String uploadServer, String uploadUser) {
		if(checkConnection()) {
			boolean a, b;
			a = sendAuthParCommand("UPLOADSERVER", uploadServer);
			b = sendAuthParCommand("UPLOADUSER", uploadUser);
			return a && b;
		}
		return false;
	}

	private String getReply(String c) {
		return _getReply(c, false);
	}
	
//	private String getAuthReply(String c) {
//		return _getReply(c, true);
//	}
	
	private String _getReply(String c, Boolean auth) {
		try {
			if(!c.equals("STATUS") && !c.equals("RECTIME"))
				System.out.println("> " + c.trim());
			String command = new String(c.trim());
			if(auth)
				command += token.getToken(c.trim(), salt);
			out.writeBytes(command + "\n");
			String reply = in.readLine();
			if(in.ready()) {
				in.readLine();
			}
			if(!c.equals("STATUS") && !c.equals("RECTIME"))
				System.out.println("< " + reply);
			return reply;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private boolean sendAuthCommand(String c) {
		return sendAuthCommandOpt(c, null);
	}
	
	private boolean sendAuthCommandOpt(String c, String opt) {
		try {
			String command = c.trim() + " ";
			if(opt != null) {
				command += opt.trim() + " ";
			}
			command += token.getToken(c.trim(), salt) + "\n";
			System.out.println("> " + command);
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
		if(this.checkConnection()) {
			sig.setPubKey(this.getPubKey());
		}
		while(loop) {
			try {
				synchronized(sig) {
					sig.wait(10000);
				}
			} catch (InterruptedException e) {
			}
			if(this.isReachable()) {
				String command = "";
				while(command != null) {
					command = null;
					sig.setConnected(true);
					if(sig.hasCommands())
						command = new String(sig.popCommand());

					if(command != null) {
						System.out.println("Got command: " + command);
						if(command.equals("ID"))
							setID(sig.getID());
						if(command.equals("EID"))
							setExamID(sig.getExamID());
						if(command.equals("SetServer")) {
							String[] server = sig.getServer();
							this.setServer(server[0], server[1]);
						}
						if(command.equals("init"))
							modeString = "init";
						if(command.equals("recTest"))
							modeString = "recTest";
						if(command.equals("rec"))
							modeString = "rec";
						if(command.equals("stop"))
							modeString = "stop";
						if(command.equals("reset"))
							modeString = "reset";
						if(command.equals("STOP_THREAD"))
							loop = false;
						if(command.equals("playFile"))
							setPlayFile(sig.getPlayFile());
						if(command.equals("playOnly"))
							setPlayOnly();
						if(command.equals("micRetry"))
							micRetry();
					}
				}
				sig.setStatus(this.status());
				sig.setTime(this.getTime());
				sig.setFileSize(this.getFileSize());
				
				Boolean isInit		= modeString.equals("init")		&& sig.getStatus().getInit();
				Boolean isRec		= modeString.equals("rec")		&& sig.getStatus().getRec();
				Boolean isRecTest	= modeString.equals("recTest");
				Boolean isRecTestR	= modeString.equals("recTestR") && sig.getStatus().getRecTestDone();
				Boolean isStop		= modeString.equals("stop")		&& sig.getStatus().getUpload();
				Boolean isReset		= modeString.equals("reset")	&& sig.getStatus().getNone();
				
				if(isInit || isRec || isStop || isReset || isRecTestR)
					modeString = "";
				
				if(modeString.equals("init")  && !sig.getStatus().getInit()) {
					if(!this.getID().equals("") && !this.getExamID().equals(""))
						this.init();
				}
				if(isRecTest) {
					this.recTest("http://ilias-intern.wiso.uni-goettingen.de/audioTest.mp3"); // TODO: REPLACE URL or make it a variable
					modeString = "recTestR";
				}
				if(modeString.equals("rec")   && !sig.getStatus().getRec())
					this.rec();
				if(modeString.equals("stop")  && !sig.getStatus().getUpload())
					this.stop();
				if(modeString.equals("reset"))
					this.reset();
			} else {
				sig.setConnected(false);
				this.checkConnection();
			}
		}
	}
}
