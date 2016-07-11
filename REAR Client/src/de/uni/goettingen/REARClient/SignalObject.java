package de.uni.goettingen.REARClient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import de.uni.goettingen.REARClient.Audio.MicrophoneLine;
import de.uni.goettingen.REARClient.Audio.Player;
import de.uni.goettingen.REARClient.Audio.Recorder;
import de.uni.goettingen.REARClient.GUI.StatusWindow;
import de.uni.goettingen.REARClient.Net.DataConnection;
import de.uni.goettingen.REARClient.Net.DownloadThread;
import de.uni.goettingen.REARClient.Net.SSH.SSHconnection;
import de.uni.goettingen.REARClient.Net.SSH.SSHkey;

public class SignalObject {
	private Boolean shutdownServer;
	private StatusWindow win;
	private MicrophoneLine micLine;
	private SSHkey sshKey;
	private File outFile;
	private DataConnection dataC;
	private String studentID;
	private String examID;
	private SSHconnection ssh;
	private String uploadServer;
	private String uploadUser;
	private PropertiesStore prop;
	private URL playFileLocation;
	private File playFile;
	private Boolean playFileDownloaded;
	private URL playTestFileLocation;
	private File playTestFile;
	private Boolean doRecord;
	private Boolean doPlay;
	private Recorder rec;
	private Recorder recMessage;
	private Player player;
	private Player messagePlayer;
	private Player voicePlayer;
	
	private Boolean audioTestDone;
	private Boolean runningAudioTest;
	
	private Object downloadSync = new Object();

	public SignalObject(StatusWindow w, MicrophoneLine ml, SSHkey ssh, PropertiesStore ps) {
		shutdownServer = false;
		win = w;
		micLine = ml;
		sshKey = ssh;
		outFile = null;
		dataC = null;
		ssh = null;
		studentID = "";
		examID = "";
		prop = ps;
		uploadServer = prop.getUploadServer();
		uploadUser = prop.getUploadServerUser();
		playFileDownloaded = false;
		doRecord = true;
		doPlay = false;
		audioTestDone = false;
		runningAudioTest = false;
		this.checkMicrophone();
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

	public synchronized void activateRecOnly() {
		doPlay = false;
		doRecord = true;
	}

	public synchronized void setAudioTestFileURL(String urlString) {
		try {
			synchronized(downloadSync) {
				playFileDownloaded = false;
			}
			playTestFileLocation = new URL(urlString);
			playTestFile = new File(prop.getDefaultPath() + "audioTest.mp3");
			DownloadThread downloadTestTh = new DownloadThread(playTestFileLocation, this, playTestFile);
			Thread dtt = new Thread(downloadTestTh);
			dtt.start();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public synchronized void setAudioFileURL(String urlString) {
		try {
			// System.out.println("Setting audio URL = " + urlString);
			playFileLocation = new URL(urlString);
			playFile = new File(prop.getDefaultPath() + "playback.mp3");
			DownloadThread downloadTh = new DownloadThread(playFileLocation, this, playFile);
			Thread dt = new Thread(downloadTh);
			dt.start();
		} catch (MalformedURLException e) {
			// Should not happen, checks done in Controller
			e.printStackTrace();
		}
	}

	public void finishedAudioDownload() {
		synchronized(downloadSync) {
			playFileDownloaded = true;
		}
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
		if (!this.getMicStatus())
			return -1;
		int mode = new Integer(win.getMode());
		synchronized(downloadSync) {
			if (mode == 1 && doPlay && !playFileDownloaded)
				mode -= 1;
		}
		return mode;
	}

	public synchronized String getID() {
		studentID = win.getID();
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
		if (!micLine.isOpen())
			micLine.open();
	}

	public synchronized void initClient() {
		ssh = new SSHconnection(uploadServer, uploadUser, sshKey, prop);
		dataC = new DataConnection("127.0.0.1", prop.getDataPort(), ssh);
		dataC.addSignal(this);
		win.init();
	}

	public synchronized String getPubKeyString() {
		return sshKey.getPubKeyString();
	}

	public synchronized void startAudioTest() {
		String recPath;
		if(!runningAudioTest) {
			audioTestDone = false;
			runningAudioTest = true;
			System.out.println("Starting audio test");
			win.startAudioTest();
			recPath = new String(prop.getAudioPath() + "audioTest.flac");
			System.out.println("  downloading file");
			
			Boolean cont = true;
			while (cont) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					;
				}
				synchronized(downloadSync) {
					cont = !playFileDownloaded;
				}
			}
			System.out.print("  playing message");
			messagePlayer = new Player(playTestFile, null);
			System.out.print("..."); System.out.flush();
			while (!messagePlayer.isDone()) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					;
				}
			}
			System.out.println("  recording sample");
			recMessage = new Recorder(micLine, new File(recPath), true);
			Thread recThread = new Thread(recMessage);
			recThread.start();
			try {
				TimeUnit.SECONDS.sleep(15);
			} catch (Exception e) {
				;
			}
			recMessage.stopRecording();
			System.out.println("    stopped recording");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (Exception e) {
				;
			}
			micLine.close();
			System.out.println("  play back recording");
			voicePlayer = new Player(new File(recPath), null);
			while (!voicePlayer.isDone()) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					;
				}
			}
			System.out.println("  done");
			micLine.open();
			audioTestDone = true;
			runningAudioTest = false;
			win.finishedAudioTest();
		}
	}

	public synchronized Boolean getAudioTestDone() {
		if(audioTestDone && ! runningAudioTest)
			return true;
		return false;
	}
	
	public synchronized void startRecording() {
		String path;
		if (win.getExamID() != null && !win.getExamID().equals("")) {
			path = new String(prop.getAudioPath() + win.getExamID().replaceAll("[/\"\'|\\\\:\\*\\?<>]", "-") + "\\");
			File p = new File(path);
			p.mkdirs();
		} else
			path = new String(prop.getAudioPath());
		System.out.println(path);
		System.out.println(win.getExamID());
		if (win.getID() != null && !win.getID().equals(""))
			outFile = new File(path + win.getID().replaceAll("[/\"\'|\\\\:\\*\\?<>]", "-") + ".flac");
		else
			outFile = new File(prop.getDefaultAudioFile());
		rec = new Recorder(micLine, outFile, false);
		Thread recThread = new Thread(rec);
		recThread.start();
		if (doPlay)
			player = new Player(playFile, rec);
		win.setRecording(doRecord, doPlay);
	}

	private synchronized long getRecFileSize() {
		System.out.println("Determining file Size");
		if (outFile != null && outFile.exists()) {
			long l = outFile.length();
			System.out.println("File size: " + Long.toString(l));
			return l;
		} else
			return 0;
	}

	public String getFormattedRecFileSize() {
		String	unit = "B";
		long	Lsize = getRecFileSize();
		double  size  = Lsize;
		
		if(Lsize < 2000) {
			return String.format("%4d %s", Lsize, unit);
		}
		size /= 1024;
		unit  = "kB";
		if(size < 2000) {
			return String.format("%-4.2f %s", size, unit);
		}
		size /= 1024;
		unit  = "MB";
		if(size < 2000) {
			return String.format("%-4.2f %s", size, unit);
		}
		size /= 1024;
		unit  = "GB";
		if(size < 2000) {
			return String.format("%-4.2f %s", size, unit);
		}
		size /= 1024;
		unit  = "TB";
		return String.format("%-4.2f %s", size, unit);
	}

	public synchronized void stopRecording() {
		if (doPlay)
			player.stop();
		rec.stopRecording();
		win.setUpload();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			;
		}
		if (dataC != null) {
			dataC.pushFile(outFile, studentID, examID);
		}

	}

	public synchronized void reset() {
		win.reset();
		micLine.close();
		micLine.open();
		doRecord = true;
		doPlay = false;
		synchronized(downloadSync) {
			playFileDownloaded = false;
		}
		studentID = "";
		examID = "";
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
