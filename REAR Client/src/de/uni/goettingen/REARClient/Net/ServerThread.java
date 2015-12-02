package de.uni.goettingen.REARClient.Net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

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
				String msg = in.readLine();
				String[] message = msg.trim().split(" ");

				if(allowShutdown && !message[0].equals("SHUTDOWN"))
					allowShutdown = false;

				else if(shutdown && !allowShutdown)
					shutdown = false;

				else if(message[0].equals("INIT"))
					initialize(message);

				else if(message[0].equals("REC"))
					startRecording(message);
					
				else if(message[0].equals("STOP"))
					stopRecording(message);
				
				else if(message[0].equals("RESET"))
					reset(message);
				
				else if(message[0].equals("ID"))
					setID(message);
				
				else if(message[0].equals("RECTIME"))
					out.writeBytes(signal.getTime() + "\n");
				
				else if(message[0].equals("STATUS"))
					out.writeBytes(String.valueOf(signal.getMode()));
				
				else if(message[0].equals("GETSALT"))
					out.writeBytes(remoteAddr + "\n");
					
				else if(message[0].equals("QUIT") || message[0].equals("EXIT"))
					quit = true;
				
				else if(message[0].equals("GETPUBKEY"))
					out.writeBytes(signal.getPubKeyString() + "\n");
				
				else if(message[0].equals("ALLOWSHUTDOWN") && checkShutdownToken(message))
					allowShutdown = true;
				
				else if(message[0].equals("SHUTDOWN") && checkShutdownToken(message))
					shutdown = true;
				
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

	private void initialize(String[] message) throws IOException {
		if(message.length > 1) {
			synchronized(signal) {
				if(signal.getMode() == 0) {
					AuthToken token = new AuthToken();
					if(token.isValid(message[1].trim(), message[0], remoteAddr)) {
						signal.initClient();
						out.writeBytes("OK\n");
					}
					else { // Token invalid
						out.writeBytes("Token Error\n");
					}
				}
				else { // Mode != 0 
					out.writeBytes("Already initialized\n");
				}
			}
		}
		else {
			out.writeBytes("Token Missing\n");
		}
	}

	private void startRecording(String[] message) throws IOException {
		if(message.length > 1) {
			synchronized(signal) {
				switch(signal.getMode()) {
				case 0:
					out.writeBytes("Not initialized\n");
					break;
				case 1:
					AuthToken token = new AuthToken();
					if(token.isValid(message[1].trim(), message[0], remoteAddr)) {
						signal.startRecording();
						out.writeBytes("OK\n");
					}
					else { // Token invalid
						out.writeBytes("Token Error\n");
					}
					break;
				case 2:
					out.writeBytes("Already Recording\n");
					break;
				case 3:
					out.writeBytes("Uploading Data\n");
					break;
				case 4:
					out.writeBytes("Waiting for reset\n");
					break;
				}
			}
		}
		else {
			out.writeBytes("Token Missing\n");
		}
	}
	
	private void stopRecording(String[] message) throws IOException {
		if(message.length > 1) {
			synchronized(signal) {
				switch(signal.getMode()) {
				case 0:
					out.writeBytes("Not initialized\n");
					break;
				case 1:
					out.writeBytes("Not Recording\n");
					break;
				case 2:
					AuthToken token = new AuthToken();
					if(token.isValid(message[1].trim(), message[0], remoteAddr)) {
						signal.stopRecording();
						out.writeBytes("OK\n");
					}
					else { // Token invalid
						out.writeBytes("Token Error\n");
					}
					break;
				case 3:
					out.writeBytes("Uploading Data\n");
					break;
				case 4:
					out.writeBytes("Waiting for reset\n");
					break;
				}
			}
		}
		else {
			out.writeBytes("Token Missing\n");
		}
	}
	
	private void reset(String[] message) throws IOException {
		if(message.length > 1) {
			synchronized(signal) {
				switch(signal.getMode()) {
				case 0:
					out.writeBytes("Not initialized\n");
					break;
				case 1:
					out.writeBytes("Not Recording\n");
					break;
				case 2:
					out.writeBytes("Still Recording\n");
					break;
				case 3:
					out.writeBytes("Uploading Data\n");
					break;
				case 4:
					AuthToken token = new AuthToken();
					if(token.isValid(message[1].trim(), message[0], remoteAddr)) {
						signal.reset();
						out.writeBytes("OK\n");
					}
					else { // Token invalid
						out.writeBytes("Token Error\n");
					}
					break;
				}
			}
		}
		else {
			out.writeBytes("Token Missing\n");
		}
	}
	
	private void setID(String[] message) throws IOException {
		if(message.length > 1) {
			synchronized(signal) {
				if(signal.getMode() == 0) {
					signal.setID(message[1]);
					out.writeBytes("OK\n");
				}
				else {
					out.writeBytes("Can only change ID in uninitialized client\n");
				}
			}
		}
		else {
			out.writeBytes(signal.getID());
		}
	}
	
	private Boolean checkShutdownToken(String[] message) {
		AuthToken token = new AuthToken();
		if(token.isValid(message[1].trim(), message[0], remoteAddr))
			return true;
		return false;
	}
}