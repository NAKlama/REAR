package de.uni.goettingen.REARClient.Audio;

import java.io.File;

public class Player {
	private Thread					t;
	private PlayerThread			pt;
	private Boolean					playing;
	private Recorder				rec;

	public Player(File inFile, Recorder recorder) {
		playing	= false;
		rec		= recorder;
		pt		= new PlayerThread(inFile, this, rec);
		t		= new Thread(pt);
		t.start();
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
}
