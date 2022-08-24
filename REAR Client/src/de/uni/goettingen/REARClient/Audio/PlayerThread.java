package de.uni.goettingen.REARClient.Audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class PlayerThread implements Runnable {

	private AudioFileFormat			fileFormat = null;
	private AudioFormat				audioFormat = null;
	private AudioFormat				decodedFormat = null;
//	private AudioFileFormat.Type	type;
	private AudioInputStream		inS;
	private AudioInputStream		dinS;
	private Player					player;
	private Boolean					stop;
	private Boolean					done;
	
	PlayerThread(File audioFile, Player p) {
		try {
			player.log("Insisde PlayerThread");
			stop			= false;
			done			= false;
			player			= p;
			fileFormat 		= AudioSystem.getAudioFileFormat(audioFile);
			audioFormat		= fileFormat.getFormat();
			player.log("  got file format");
//			type			= fileFormat.getType();
			
			inS 			= AudioSystem.getAudioInputStream(audioFile);
			player.log("  got audioInputStream");
			decodedFormat	= new AudioFormat(	AudioFormat.Encoding.PCM_SIGNED,
					audioFormat.getSampleRate(),
					16,
					audioFormat.getChannels(),
					audioFormat.getChannels() * 2,
					audioFormat.getSampleRate(),
					false );
			player.log("  created output format");
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void rawplay(AudioFormat targetFormat, AudioInputStream dinS) {
		player.log("Inside PlayerThread::rawplay()");
		byte[] data = new byte[4096];
		SourceDataLine line = getLine(targetFormat);
		if(line != null) {
			line.start();
			int nBytesRead		= 0;
			@SuppressWarnings("unused")
			int nBytesWritten	= 0;
			try {
				while(nBytesRead != -1 && !stop) {
					nBytesRead = dinS.read(data, 0, data.length);
					if(nBytesRead != -1) {
						nBytesWritten = line.write(data, 0, nBytesRead);
					}
//					System.out.print(".");
//					System.out.flush();
				}
				line.drain();
				line.stop();
				line.close();
				done = true;
				dinS.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private SourceDataLine getLine(AudioFormat aFormat) {
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, aFormat);
		try {
			res = (SourceDataLine) AudioSystem.getLine(info);
			res.open(aFormat);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public void stop() {
		stop = true;
	}
	
	public Boolean isDone() {
		return done;
	}
	
	@Override
	public void run() {
		player.log("Running Player thread");
		player.setPlaying(true);
		dinS	= AudioSystem.getAudioInputStream(decodedFormat, inS);
//		dinS = inS;
		rawplay(decodedFormat, dinS);
		player.setPlaying(false);
		try {
			inS.close();
			player.stopRecording();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		player.stopRecording();
	}
}
