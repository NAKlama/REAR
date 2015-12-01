package de.uni.goettingen.REARClient.Audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneLine {
	private TargetDataLine tDataLine;
	
	public MicrophoneLine() {
		AudioFormat	audioFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED, 
				44100.0f, 16, 1, 2, 44100.0f, false);
		
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
			System.out.println("Unable to get a recording line");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public TargetDataLine getLine() {
		return tDataLine;
	}
}
