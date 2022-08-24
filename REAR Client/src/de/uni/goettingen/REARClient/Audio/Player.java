package de.uni.goettingen.REARClient.Audio;

import java.io.File;

import de.uni.goettingen.REARClient.SignalObject;

public class Player {
	private Thread					t;
	private PlayerThread			pt;
	private Boolean					playing;
	private Recorder				rec;
	private SignalObject			signal;

	public Player(SignalObject sig, File inFile, Recorder recorder) {
		signal  = sig;
		playing	= false;
		rec		= recorder;
		pt		= new PlayerThread(inFile, this);
		t		= new Thread(pt);
		signal.log("New player thread");
		t.start();
	}
	
	public synchronized void log(String message) {
		signal.log(message);
	}
	
	public synchronized void setPlaying(Boolean p) {
		playing = p;
	}
	
	public synchronized Boolean getPlaying(Boolean p) {
		return playing;
	}
	
	public synchronized void stop() {
		pt.stop();
	}
	
	public synchronized Boolean isDone() {
		return pt.isDone();
	}
	
	public synchronized void stopRecording() {
		if(rec != null)
			rec.stopRecording();
	}
}
