package de.uni.goettingen.REARClient;

import java.io.File;

import de.uni.goettingen.REARClient.Audio.MicrophoneLine;
import de.uni.goettingen.REARClient.Audio.Recorder;
import de.uni.goettingen.REARClient.GUI.StatusWindow;
import de.uni.goettingen.REARClient.Net.SSH.SSHkey;

public class SignalObject {
	private Boolean			shutdownServer;
	private StatusWindow	win;
	private Recorder		rec;
	private MicrophoneLine	micLine;
	private SSHkey			sshKey;


	public SignalObject(StatusWindow w, MicrophoneLine ml, SSHkey ssh) {
		shutdownServer	= false;
		win				= w;
		micLine			= ml;
		sshKey			= ssh;
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
		return win.getID();
	}
	
	public String getTime() {
		return win.getTime();
	}
	
	public void setID(String id) {
		win.setID(id);
	}

	public void initClient() {
		win.init();
	}
	
	public String getPubKeyString() {
		return sshKey.getPubKeyString();
	}
	
	public void startRecording() {
		File outFile;
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
		// TODO Upload File
	}
	
	public void reset() {
		win.reset();
	}

	public void setExamID(String id) {
		win.setExamID(id);
		
	}

	public String getExamID() {
		return win.getExamID();
	}	
}
