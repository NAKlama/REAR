package de.uni.goettingen.REARClient.Audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneLine {
	private static final float	SAMPLERATE		= 44100.0f;
	private static final int	BITWIDTH		= 16;
	private static final int	CHANNELS		= 1;
	
	private TargetDataLine	tDataLine;
	private Boolean			lineOpen;
	
	public MicrophoneLine() {
		lineOpen = false;
		open();
	}
	
	public void open() {
		lineOpen = true;
		AudioFormat	audioFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED, 
				SAMPLERATE,
				BITWIDTH,
				CHANNELS, 
				(BITWIDTH / 8) * CHANNELS,
				SAMPLERATE, 
				false);
		
		DataLine.Info	info = new DataLine.Info(TargetDataLine.class, audioFormat);
		tDataLine = null;
		try
		{
			tDataLine = (TargetDataLine) AudioSystem.getLine(info);
			tDataLine.open(audioFormat);
			tDataLine.start();
		}
		catch (LineUnavailableException e)
		{
			lineOpen = false;
			System.out.println("Unable to get a recording line");
		}
	}
	
	public Boolean isOpen() {
		return lineOpen;
	}
	
	public TargetDataLine getLine() {
		if(lineOpen)
			return tDataLine;
		else
			return null;
	}
}
