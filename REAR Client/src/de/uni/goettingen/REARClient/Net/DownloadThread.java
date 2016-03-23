package de.uni.goettingen.REARClient.Net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import de.uni.goettingen.REARClient.SignalObject;

public class DownloadThread implements Runnable {
	private URL				url;
	private SignalObject	sig;
	private File			outFile;
	
	public DownloadThread(URL urlIn, SignalObject sigIn, File file) {
		url		= urlIn;
		sig		= sigIn;
		outFile	= file;
	}
	
	@Override
	public void run() {
		URLConnection con = null;
		int i;
		
		try {
			con = url.openConnection(); 
			BufferedInputStream  bis = new BufferedInputStream(con.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(
										new FileOutputStream(outFile.getName()));
			while((i = bis.read()) != -1) {
				bos.write(i);
			}
			bos.flush();
			bis.close();
			bos.close();
			sig.finishedAudioDownload();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
