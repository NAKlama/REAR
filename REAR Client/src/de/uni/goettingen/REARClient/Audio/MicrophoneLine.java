package de.uni.goettingen.REARClient.Audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import de.uni.goettingen.REARClient.SignalObject;

public class MicrophoneLine {
	private static final float	SAMPLERATE		= 44100.0f;
	private static final int	BITWIDTH		= 16;
	private static final int	CHANNELS		= 1;

	private TargetDataLine	tDataLine;
	private Boolean			lineOpen;
	private SignalObject	signal;

	public MicrophoneLine() {
		lineOpen = false;
	}

	private void log(String message) {
		if(signal != null)
			signal.log(message);
		else
			System.out.println(message);
	}
	
	public boolean open() {
		if(lineOpen && tDataLine != null) {
			tDataLine.close();
		}
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
		catch (LineUnavailableException | IllegalArgumentException e)
		{
			lineOpen = false;
			log("Unable to get a recording line");
			return false;
		}

		return true;
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

	public void close() {
		if(lineOpen) {
			tDataLine.close();
			lineOpen = false;
		}
	}

	public void setSignalObject(SignalObject signal) {
		this.signal = signal;
		
	}
}
