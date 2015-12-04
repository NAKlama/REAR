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
		File outFile    	= new File(REARclient.DEFAULT_PATH + win.getID() + ".flac");
		rec					= new Recorder(micLine, outFile);
		Thread recThread	= new Thread(rec);
		recThread.start();
		win.setRecording();
	}
	
	public void stopRecording() {
		rec.stopRecording();
		win.setUpload();
		// Upload File
	}
	
	public void reset() {
		win.reset();
	}
	
}
