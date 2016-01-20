package de.uni.goettingen.REARClient;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


import com.jcraft.jsch.JSch;

import de.uni.goettingen.REARClient.Audio.MicrophoneLine;
import de.uni.goettingen.REARClient.GUI.StatusWindow;
import de.uni.goettingen.REARClient.Net.ServerThread;
import de.uni.goettingen.REARClient.Net.SSH.SSHkey;

public class REARclient {
	public static final boolean DEBUG					= false;
	public static final boolean DEBUG_GUI				= false;
	public static final String	DEFAULT_PATH			= "D:\\REAR\\audio\\";
	public static final String	DEFAULT_FILE			= "D:\\REAR\\audio\\default.flac";
	
	private static final int	TCP_PORT				= 15000;	
	public  static final int	DATA_PORT				= 28947;
	public  static final String	UPLOAD_SERVER			= "134.76.185.241";
//	public  static final String	UPLOAD_SERVER			= "134.76.187.188";
//	public  statuc final String	UPLOAD_SERVER			= "192.168.246.132";
	public  static final String	UPLOAD_SERVER_USER		= "REAR";
	
	public static void main(String[] args) throws Exception
	{
		run();
	}
	
	public static void run() throws Exception
	{
		MicrophoneLine	micLine			= new MicrophoneLine();
		StatusWindow	win				= new StatusWindow(micLine);
		
		File outFile = new File(DEFAULT_PATH);
		outFile.mkdirs();

		SSHkey			sshKey			= new SSHkey(new JSch());
		
		ServerSocket server = new ServerSocket(TCP_PORT, 10, InetAddress.getByName("0.0.0.0"));
		
		SignalObject signal = new SignalObject(win, micLine, sshKey);
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
