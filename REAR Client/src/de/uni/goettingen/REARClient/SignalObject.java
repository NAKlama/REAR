package de.uni.goettingen.REARClient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import de.uni.goettingen.REARClient.Audio.MicrophoneLine;
import de.uni.goettingen.REARClient.Audio.Player;
import de.uni.goettingen.REARClient.Audio.Recorder;
import de.uni.goettingen.REARClient.GUI.StatusWindow;
import de.uni.goettingen.REARClient.Net.DataConnection;
import de.uni.goettingen.REARClient.Net.DownloadThread;
import de.uni.goettingen.REARClient.Net.SSH.SSHconnection;
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
	private SSHconnection	ssh;
	private String			uploadServer;
	private String			uploadUser;
	private PropertiesStore	prop;
	private URL				playFileLocation;
	private File			playFile;
	private Boolean			playFileDownloaded;
	private Boolean			doRecord;
	private Boolean			doPlay;
	private Player			player;

	public SignalObject(StatusWindow w, MicrophoneLine ml, SSHkey ssh, PropertiesStore ps) {
		shutdownServer		= false;
		win					= w;
		micLine				= ml;
		sshKey				= ssh;
		outFile				= null;
		dataC				= null;
		ssh					= null;
		studentID			= "";
		examID				= "";
		prop				= ps;
		uploadServer		= prop.getUploadServer();
		uploadUser			= prop.getUploadServerUser();
		playFileDownloaded	= false;
		doRecord			= true;
		doPlay				= false;
	}
	
	public synchronized Boolean getDoPlay() {
		return doPlay;
	}
	
	public synchronized Boolean getDoRecord() {
		return doRecord;
	}

	public synchronized void activatePlayAudio() {
		doPlay = true;
	}

	public synchronized void activatePlayNoRecord() {
		doPlay = true;
		doRecord = false;
	}
	
	public synchronized void setAudioFileURL(String urlString) {
		try {
			playFileLocation = new URL(urlString);
			playFile = new File(prop.getDefaultPath() + "playback.flac");
			DownloadThread downloadTh = new DownloadThread(playFileLocation, this, playFile);
			Thread dt = new Thread(downloadTh);
			dt.start();
		} catch (MalformedURLException e) {
			// Should not happen, checks done in Controller
			e.printStackTrace();
		}
	}
	
	public synchronized void finishedAudioDownload() {
		playFileDownloaded = true;
	}
	
	public synchronized void setUploadServer(String uls) {
		uploadServer = uls;
	}

	public synchronized String getUploadServer() {
		return uploadServer;
	}

	public synchronized void setUploadUser(String ulu) {
		uploadUser = ulu;
	}

	public synchronized String getUploadUser() {
		return uploadUser;
	}

	public synchronized void shutdown() {
			shutdownServer = true;
	}

	public synchronized Boolean getShutdownStatus() {
		return shutdownServer;
	}

	public synchronized int getMode() {
		int mode = new Integer(win.getMode());
		if(mode == 1 && doPlay && !playFileDownloaded)
			mode -= 1;
		return mode;
	}

	public synchronized String getID() {
		studentID =  win.getID();
		return studentID;
	}

	public synchronized String getTime() {
		return win.getTime();
	}
	
	public synchronized Boolean getMicStatus() {
		return micLine.isOpen();
	}

	public synchronized void setID(String id) {
		win.setID(new String(id));
		studentID = new String(id);
	}
	
	public synchronized void checkMicrophone() {
		if(!micLine.isOpen())
			micLine.open();
	}

	public synchronized void initClient() {
		ssh				= new SSHconnection(uploadServer, uploadUser, sshKey, prop);
		dataC			= new DataConnection("127.0.0.1", prop.getDataPort(), ssh);
		dataC.addSignal(this);
		win.init();
	}

	public synchronized String getPubKeyString() {
		return sshKey.getPubKeyString();
	}

	public synchronized void startRecording() {
		String path;
		if(win.getExamID() != null && ! win.getExamID().equals("")) {
			path = new String(prop.getAudioPath() + win.getExamID().replaceAll("[/\"\'|\\\\:\\*\\?<>]", "-") + "\\");
			File p = new File(path);
			p.mkdirs();
		}
		else 
			path = new String(prop.getAudioPath());
		System.out.println(path);
		System.out.println(win.getExamID());
		if(win.getID() != null && ! win.getID().equals(""))
			outFile			= new File(path + win.getID().replaceAll("[/\"\'|\\\\:\\*\\?<>]", "-") + ".flac");
		else
			outFile			= new File(prop.getDefaultAudioFile());
		rec					= new Recorder(micLine, outFile);
		Thread recThread	= new Thread(rec);
		recThread.start();
		player 				= new Player(playFile, rec);
		win.setRecording(doRecord, doPlay);
	}

	public synchronized void stopRecording() {
		player.stop();
		rec.stopRecording();
		win.setUpload();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {;}
		if(dataC != null) {
			dataC.pushFile(outFile, studentID, examID);
		}
		
	}

	public synchronized void reset() {
		win.reset();
		micLine.open();
		doRecord			= true;
		doPlay				= false;
		playFileDownloaded	= false;
		studentID			= "";
		examID				= "";
	}

	public synchronized void setExamID(String id) {
		win.setExamID(new String(id));
		examID = id;
	}

	public synchronized String getExamID() {
		examID = win.getExamID();
		return examID;
	}

	public synchronized void finishedDownload() {
		win.setOK();
	}
}
