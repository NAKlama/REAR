package de.uni.goettingen.REARClient.Audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import javaFlacEncoder.FLACFileWriter;

public class Recorder implements Runnable {
	private TargetDataLine			line;
	private AudioInputStream		aInputStream;
	private File					outFile;
	private Boolean					wave;

	public Recorder(MicrophoneLine l, File f, Boolean w)
	{
		line			= l.getLine();
		aInputStream	= new AudioInputStream(line);
		outFile			= f;
		wave			= w;
	}
	
	public void setFile(File f)
	{
		outFile = f;
	}
	
	public void run()
	{
		try
		{
			if(wave)
				AudioSystem.write(aInputStream, AudioFileFormat.Type.WAVE, outFile);
			else
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
