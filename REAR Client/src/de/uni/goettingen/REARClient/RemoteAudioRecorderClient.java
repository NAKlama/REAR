package de.uni.goettingen.REARClient;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


import com.jcraft.jsch.JSch;

import de.uni.goettingen.REARClient.Audio.MicrophoneLine;
import de.uni.goettingen.REARClient.GUI.StatusWindow;
import de.uni.goettingen.REARClient.Net.DataConnection;
import de.uni.goettingen.REARClient.Net.ServerThread;
import de.uni.goettingen.REARClient.Net.SSH.SSHconnection;
import de.uni.goettingen.REARClient.Net.SSH.SSHkey;

public class RemoteAudioRecorderClient {
	public static final boolean DEBUG					= true;
	public static final boolean DEBUG_GUI				= true;
	public static final String	DEFAULT_PATH			= "C:\\tmp\\audio\\";
	public static final String	DEFAULT_FILE			= "C:\\tmp\\audio\\default.flac";
	
	private static final int	TCP_PORT				= 15000;	
	private static final int	DATA_PORT				= 15001;
//	private static final String	UPLOAD_SERVER			= "134.76.187.188";
	private static final String	UPLOAD_SERVER			= "192.168.246.132";
	private static final String	UPLOAD_SERVER_USER		= "audioUpload";
	
	public static void main(String[] args) throws Exception
	{
		run();
	}
	
	public static void run() throws Exception
	{
		MicrophoneLine	micLine			= new MicrophoneLine();
		StatusWindow	win				= new StatusWindow(micLine);
		
		File outFile = new File(DEFAULT_FILE);
		outFile.mkdirs();

		SSHkey			sshKey			= new SSHkey(new JSch());
		SSHconnection	ssh				= new SSHconnection(UPLOAD_SERVER, UPLOAD_SERVER_USER, sshKey, DATA_PORT);
//		DataConnection	dataC			= new DataConnection(EXAM_SERVER, DATA_PORT, ssh);
		@SuppressWarnings("unused")
		DataConnection	dataC			= new DataConnection("127.0.0.1", DATA_PORT, ssh);
		
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
