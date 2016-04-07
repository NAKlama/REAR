package de.uni.goettingen.REARClient;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.jcraft.jsch.JSch;

import de.uni.goettingen.REARClient.Audio.MicrophoneLine;
import de.uni.goettingen.REARClient.GUI.StatusWindow;
import de.uni.goettingen.REARClient.Net.ServerThread;
import de.uni.goettingen.REARClient.Net.SSH.SSHkey;

public class REARclient {
	public static final boolean DEBUG					= false;
	public static final boolean DEBUG_GUI				= false;
	
	
	public static final ArrayList<File> configDirs		= new ArrayList<>();;
	
	public static void main(String[] args) throws Exception
	{
		configDirs.add(new File("C:\\Users\\ilias\\REAR"));
		configDirs.add(new File("C:\\tmp"));
		run();
	}
	
	public static void run() throws Exception
	{
		PropertiesStore prop			= new PropertiesStore();
		MicrophoneLine	micLine			= new MicrophoneLine();
		StatusWindow	win				= new StatusWindow();
		
		prop.load(configDirs);
		
		File outFile = new File(prop.getAudioPath());
		outFile.mkdirs();
		
		SSHkey			sshKey			= new SSHkey(new JSch(), prop);
		
		ServerSocket server = new ServerSocket(prop.getListenPort(), 10, InetAddress.getByName("0.0.0.0"));
		
		SignalObject signal = new SignalObject(win, micLine, sshKey, prop);
		win.setSignalObject(signal);
		
		while(! signal.getShutdownStatus())
		{
			Socket conn = server.accept();
			Thread serverThread = new Thread(new ServerThread(conn, signal));
			serverThread.start();
		}
		
		server.close();
	}
}
