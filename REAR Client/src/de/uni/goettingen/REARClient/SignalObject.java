package de.uni.goettingen.REARClient;

import java.io.File;

import de.uni.goettingen.REARClient.Audio.MicrophoneLine;
import de.uni.goettingen.REARClient.Audio.Recorder;
import de.uni.goettingen.REARClient.GUI.StatusWindow;
import de.uni.goettingen.REARClient.Net.DataConnection;
import de.uni.goettingen.REARClient.Net.SSH.SSHkey;

public class SignalObject {
	private Boolean			shutdownServer;
	private StatusWindow	win;
	private Recorder		rec;
	private MicrophoneLine	micLine;
	private SSHkey			sshKey;
	private File 			outFile;
	private DataConnection	dataC;
	private String			studentID;
	private String			examID;

	public SignalObject(StatusWindow w, MicrophoneLine ml, SSHkey ssh, DataConnection dc) {
		shutdownServer	= false;
		win				= w;
		micLine			= ml;
		sshKey			= ssh;
		outFile			= null;
		dataC			= dc;
		studentID		= "";
		examID			= "";
	}

	public void shutdown() {
		synchronized(this) {
			shutdownServer = true;
		}
	}

	public Boolean getShutdownStatus() {
		return shutdownServer;
	}
	
	public int getMode() {
		return win.getMode();
	}
	
	public String getID() {
		studentID =  win.getID();
		return studentID;
	}
	
	public String getTime() {
		return win.getTime();
	}
	
	public void setID(String id) {
		win.setID(id);
		studentID = id;
	}

	public void initClient() {
		win.init();
	}
	
	public String getPubKeyString() {
		return sshKey.getPubKeyString();
	}
	
	public void startRecording() {
		
		String path;
		if(win.getExamID() != null && ! win.getExamID().equals("")) {
			path = REARclient.DEFAULT_PATH + win.getExamID() + "\\";
			File p = new File(path);
			p.mkdirs();
		}
		else 
			path = REARclient.DEFAULT_PATH;
		System.out.println(path);
		System.out.println(win.getExamID());
		if(win.getID() != null && ! win.getID().equals(""))
			outFile				= new File(path + win.getID() + ".flac");
		else
			outFile				= new File(REARclient.DEFAULT_FILE);
		rec					= new Recorder(micLine, outFile);
		Thread recThread	= new Thread(rec);
		recThread.start();
		win.setRecording();
	}
	
	public void stopRecording() {
		rec.stopRecording();
		win.setUpload();
		if(dataC != null) {
			dataC.pushFile(outFile, studentID, examID);
		}
	}
	
	public void reset() {
		win.reset();
	}

	public void setExamID(String id) {
		win.setExamID(id);
		examID = id;
	}

	public String getExamID() {
		examID = win.getExamID();
		return examID;
	}

	public void finishedDownload() {
		win.setOK();
	}
}
