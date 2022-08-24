package de.uni.goettingen.REARClient.Net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import de.uni.goettingen.REARClient.SignalObject;

public class DataConnection implements Runnable {
	private File			uploadPath;
	private	File			file;
	private SignalObject	signal;
	private String			studentID;
	private String			examID;
	
	public DataConnection(File path) {
		uploadPath = path;
		signal  = null;
		
	}
	
	public void addSignal(SignalObject s) {
		signal = s;
	}
	
	private void log(String message) {
		if(signal != null)
			signal.log(message);
		else
			System.out.println(message);
	}
	
	public void pushFile(File f, String sID, String eID) {
		file		= f;
		studentID	= sID.trim().replaceAll("[/\"\'|\\\\:\\*\\?<>]", "-");
		examID		= eID.trim().replaceAll("[/\"\'|\\\\:\\*\\?<>]", "-");
		log("push " + f.getAbsolutePath());
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		try {
			log("  inside Thread");
			String				filename    = examID + "_" + studentID + ".flac";
			File 				outFile		= new File(uploadPath, filename);
			log("  copy: " + file.getAbsolutePath() + " => " + outFile.getAbsolutePath());
			copyFile(file, outFile);
			
			Thread.sleep(3000);
			
			signal.finishedDownload();
			
			log("  closing Thread");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	private void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}
