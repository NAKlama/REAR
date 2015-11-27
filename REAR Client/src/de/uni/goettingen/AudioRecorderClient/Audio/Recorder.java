package de.uni.goettingen.AudioRecorderClient.Audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import javaFlacEncoder.FLACFileWriter;

public class Recorder implements Runnable {
	private TargetDataLine			line;
	private AudioInputStream		aInputStream;
	private File					outFile;

	public Recorder(MicrophoneLine l, File f)
	{
		line			= l.getLine();
		aInputStream	= new AudioInputStream(line);
		outFile			= f;
	}
	
	public void setFile(File f)
	{
		outFile = f;
	}
	
	public void run()
	{
		try
		{
//			line.start();
			AudioSystem.write(aInputStream, FLACFileWriter.FLAC, outFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void stopRecording()
	{
		line.stop();
		line.close();
	}
}
