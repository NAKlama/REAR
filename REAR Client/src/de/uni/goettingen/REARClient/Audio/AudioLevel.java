package de.uni.goettingen.REARClient.Audio;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.TargetDataLine;

public class AudioLevel extends Thread {
	private AudioInputStream	aStream;
	private byte []				buffer;
	
	public AudioLevel(TargetDataLine line)
	{
		aStream 	= new AudioInputStream(line);
		buffer		= new byte [200];
		start();
	}
	
	public float getLevel()
	{
		float max = 0.0f;
		for(int i=0; i<199; i+=2)
		{
			short v = (short) (buffer[i] + (buffer[i+1] << 8));
//			float val = (float) Math.log10((Math.abs(((float) v))+1.0f) / 3276.9f);
			float val = Math.abs((float) v) / 32769.0f;
			max = Math.max(max, val);
		}
		return max;
	}
	
	public void run()
	{
		while(true)
			try {
				aStream.read(buffer, 0, buffer.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}	
}
