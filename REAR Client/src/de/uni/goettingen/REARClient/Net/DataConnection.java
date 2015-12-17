package de.uni.goettingen.REARClient.Net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import de.uni.goettingen.REARClient.SignalObject;
import de.uni.goettingen.REARClient.Net.SSH.SSHconnection;

public class DataConnection implements Runnable {
	private String			host;
	private int				port;
	private	File			file;
	private SSHconnection	ssh;
	private SignalObject	signal;
	private String			studentID;
	private String			examID;
	
	public DataConnection(String h, int p, SSHconnection s) {
		host 	= h;
		port 	= p;
		ssh  	= s;
		signal  = null;
		
	}
	
	public void addSignal(SignalObject s) {
		signal = s;
	}
	
	public void pushFile(File f, String sID, String eID) {
		file		= f;
		studentID	= sID.trim().replaceAll("[/\"\'|\\\\:\\*\\?<>]", "-") + "\n";
		examID		= eID.trim().replaceAll("[/\"\'|\\\\:\\*\\?<>]", "-") + "\n";
		System.out.println("push " + f.getAbsolutePath());
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		try {
			System.out.println("  inside Thread");
			ssh.ensureConnection();
			System.out.println("  connecting to "+host+":"+port);
			Socket 				sock		= new Socket(host, port);
			DataOutputStream	out			= new DataOutputStream(sock.getOutputStream());
			FileInputStream		fStr		= new FileInputStream(file);
			byte[] 				buff 		= new byte[1024];
			int    				rsize;
			int					total = 0;
			out.write(studentID.getBytes(), 0, studentID.length());
			out.write(examID.getBytes(), 0, examID.length());
			while((rsize = fStr.read(buff, 0, 1024)) > 0) {
//				System.out.println("  rsize = " + rsize);
				out.write(buff, 0, rsize);
				total += rsize;
			}
			fStr.close();
			System.out.println("   size " + total);
			Thread.sleep(3000);
			out.close();
			sock.close();
			
			signal.finishedDownload();
			
			System.out.println("  closing Thread");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
