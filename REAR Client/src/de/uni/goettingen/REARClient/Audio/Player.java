package de.uni.goettingen.REARClient.Audio;

import java.io.File;

public class Player {
	private Thread					t;
	private PlayerThread			pt;
	private Boolean					playing;

	Player(File inFile) {
		playing	= false;
		pt		= new PlayerThread(inFile, this);
		t		= new Thread(pt);
		t.start();
	}
	
	public synchronized void setPlaying(Boolean p) {
		playing = p;
	}
	
	public synchronized Boolean getPlaying(Boolean p) {
		return playing;
	}
}
