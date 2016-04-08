package de.uni.goettingen.REARClient.Net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uni.goettingen.REARClient.REARclient;
import de.uni.goettingen.REARClient.SignalObject;

public class ServerThread implements Runnable {
	private Socket			conn;
	private SignalObject	signal;

	private Boolean quit;
	private Boolean shutdown;
	private Boolean allowShutdown;

	private BufferedReader   	in;
	private DataOutputStream 	out;

	private String		remoteAddr;

	public ServerThread(Socket sock, SignalObject sig) {
		conn			= sock;
		signal			= sig;
		quit 			= false;
		shutdown 		= false;
		allowShutdown 	= false;
	}
	@Override
	public void run() {
		try {
			in			= new BufferedReader(new InputStreamReader(conn.getInputStream()));
			out			= new DataOutputStream(conn.getOutputStream());
			remoteAddr	= conn.getInetAddress().toString() + ":" + conn.getPort();
			if(REARclient.DEBUG)
				System.out.println("Running connection thread connected to " + remoteAddr + "#\n");

			while(!quit) {
				String msg = new String(in.readLine());
				ArrayList<String> message = new ArrayList<>();
				int msgLength = msg.trim().split(" ").length;
				for(int i = 0; i<msgLength; i++) {
					message.add(msg.trim().split(" ")[i]);
				}
				
				if(REARclient.DEBUG)
					System.out.println("< " + msg);

				if(allowShutdown && !message.get(0).equals("SHUTDOWN"))
					allowShutdown = false;

				else if(shutdown && !allowShutdown)
					shutdown = false;

				else if(message.get(0).equals("INIT"))
					initialize(message);

				else if(message.get(0).equals("REC"))
					startRecording(message);
					
				else if(message.get(0).equals("STOP"))
					stopRecording(message);
				
				else if(message.get(0).equals("RESET"))
					reset(message);
				
				else if(message.get(0).equals("ID"))
					setID(message);
				
				else if(message.get(0).equals("EXAMID"))
					examID(message);
				
				else if(message.get(0).equals("RECTIME")) {
					String outStr = signal.getTime() + "\n";
					System.out.println("> " + outStr.trim());
					out.writeBytes(outStr);
				}
				
				else if(message.get(0).equals("STATUS")) {
					String outStr = new String();
					outStr = String.valueOf(signal.getMode()) + "\n";
					System.out.println("> " + outStr.trim());
					out.writeBytes(outStr);
				}
				
				else if(message.get(0).equals("GETSALT"))
					out.writeBytes(remoteAddr + "\n");
					
				else if(message.get(0).equals("QUIT") || message.get(0).equals("EXIT"))
					quit = true;
				
				else if(message.get(0).equals("GETPUBKEY"))
					out.writeBytes(signal.getPubKeyString() + "\n");
				
				else if(message.get(0).equals("ALLOWSHUTDOWN") && checkShutdownToken(message))
					allowShutdown = true;
				
				else if(message.get(0).equals("SHUTDOWN") && checkShutdownToken(message))
					shutdown = true;
				
				else if(message.get(0).equals("UPLOADSERVER"))
					uploadServer(message);
				
				else if(message.get(0).equals("UPLOADUSER"))
					uploadUser(message);
				else if(message.get(0).equals("MICSTATUS")) {
					if(signal.getMicStatus())
						out.writeBytes("True\n");
					else
						out.writeBytes("False\n");
				}
				
				else if(message.get(0).equals("MICRETRY"))
					checkMicrophone(message);
				else if(message.get(0).equals("PLAYFILE"))
					fetchAudioFile(message);
				else if(message.get(0).equals("PLAYONLY"))
					playOnly(message);
			}
		} catch(NullPointerException e) {
			; // Do nothing
		}
		catch(SocketException e) {
			; // Do nothing
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Boolean tokenCheck(String token, String cmd, String salt, List<Integer> allowedModes) throws IOException {
		if(allowedModes != null && allowedModes.size() > 0) {
			Boolean found = false;
			for(Integer mode : allowedModes) 
				if(signal.getMode() == mode)
					found = true;
			if(!found) {
				switch(signal.getMode()) {
				case 0:
					out.writeBytes("Not initialized  ");
					break;
				case 1:
					out.writeBytes("Not Recording  ");
					break;
				case 2:
					out.writeBytes("Still Recording  ");
					break;
				case 3:
					out.writeBytes("Uploading Data  ");
					break;
				case 4:
					out.writeBytes("Waiting for reset  ");
					break;
				}
				out.writeBytes("Wrong Mode\n");
				return false;
			}
		}
		AuthToken tokenObj = new AuthToken();
		if(tokenObj.isValid(token, cmd, salt)) {
			out.writeBytes("OK\n");
			return true;
		}
		else
			out.writeBytes("Token Error\n");
		return false;
	}
	
	private void fetchAudioFile(ArrayList<String> message) throws IOException {
//		System.out.println("fetchAudioFile(); message.size() = " + message.size() + " : " + message.get(1));
		if(message.size() > 2) {
			synchronized(signal) {
				if(tokenCheck(message.get(2).trim(), message.get(0), remoteAddr, Arrays.asList(0))) {
					signal.setAudioFileURL(message.get(1));
					signal.activatePlayAudio();
				}
			}
		}
	}
	
	private void checkMicrophone(ArrayList<String> message) throws IOException {
		if(message.size() > 1) {
			synchronized(signal) {
				if(tokenCheck(message.get(1).trim(), message.get(0), remoteAddr, null)) {
					signal.checkMicrophone();
				}
			}
		}
	}
	
	private void playOnly(ArrayList<String> message) throws IOException {
		if(message.size() > 1) {
			synchronized(signal) {
				if(tokenCheck(message.get(1).trim(), message.get(0), remoteAddr, Arrays.asList(0))) {
					signal.activatePlayNoRecord();
				}
			}
		}
	}
	
	private void initialize(ArrayList<String> message) throws IOException {
		if(message.size() > 1) 
			synchronized(signal) {
				if(tokenCheck(message.get(1).trim(), message.get(0), remoteAddr, Arrays.asList(0))) 
					signal.initClient();
			}
	}

	private void startRecording(ArrayList<String> message) throws IOException {
		if(message.size() > 1) 
			synchronized(signal) {
				if(tokenCheck(message.get(1).trim(), message.get(0), remoteAddr, Arrays.asList(1)))
					signal.startRecording();
			}
	}
	
	private void stopRecording(ArrayList<String> message) throws IOException {
		if(message.size() > 1) 
			synchronized(signal) {
				if(tokenCheck(message.get(1).trim(), message.get(0), remoteAddr, Arrays.asList(2)))
					signal.stopRecording();
			}
	}
	
	private void reset(ArrayList<String> message) throws IOException {
		if(message.size() > 1)
			synchronized(signal) {
				if(tokenCheck(message.get(1).trim(), message.get(0), remoteAddr, null))
					signal.reset();
			}
	}
	
	private void setID(ArrayList<String> message) throws IOException {
		if(message.size() > 1) {
			System.out.println("setID(); message.size() = " + message.size() + " : " + message.get(1));
			synchronized(signal) {
				if(signal.getMode() == 0) {
					signal.setID(message.get(1));
					out.writeBytes("OK\n");
				}
				else {
					out.writeBytes("Can only change ID in uninitialized client\n");
				}
			}
		}
		else {
			out.writeBytes(signal.getID() + "\n");
		}
	}
	
	private void examID(ArrayList<String> message) throws IOException {
		if(message.size() > 1) {
			System.out.println("examID(); message.size() = " + message.size() + " : " + message.get(1));
			synchronized(signal) {
				if(signal.getMode() == 0) {
					signal.setExamID(message.get(1));
					out.writeBytes("OK\n");
				}
				else {
					out.writeBytes("Can only change ExamID in uninitialized client\n");
				}
			}
		}
		else {
			out.writeBytes(signal.getExamID() + "\n");
		}
	}
	
	private void uploadServer(ArrayList<String> message) throws IOException {
		if(message.size() > 1) 
			synchronized(signal) {
				if(tokenCheck(message.get(2).trim(), message.get(0), remoteAddr, Arrays.asList(0)))
					signal.setUploadServer(message.get(1));
						
				}
	}
	
	private void uploadUser(ArrayList<String> message) throws IOException {
		if(message.size() > 1)
			synchronized(signal) {
				if(tokenCheck(message.get(2).trim(), message.get(0), remoteAddr, Arrays.asList(0)))
					signal.setUploadUser(message.get(1));
						
				}
	}
	
	private Boolean checkShutdownToken(ArrayList<String> message) {
		AuthToken token = new AuthToken();
		if(token.isValid(message.get(1).trim(), message.get(0), remoteAddr))
			return true;
		return false;
	}
}