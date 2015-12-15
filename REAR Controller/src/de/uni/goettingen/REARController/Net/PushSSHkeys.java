package de.uni.goettingen.REARController.Net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class PushSSHkeys implements Runnable {
	private JSch	jsch;
	private Session ssh;
	
	private File	file;
	
	public PushSSHkeys() {
		jsch=new JSch();
	}
	
	public void push(String user, String server, File f, String to) {
		file = f;
		try {
			ssh	= jsch.getSession(user, server, 22);
			UserInfo	ui		= new SSHUserInfo();
			ssh.setUserInfo(ui);
			ssh.setConfig("StrictHostKeyChecking", "no");
			ssh.setPortForwardingL(28947, "127.0.0.1", 28947);
			ssh.connect();
			Thread t = new Thread(this);
			t.start();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			Socket 				sock		= new Socket("127.0.0.1", 28947);
			DataOutputStream	out			= new DataOutputStream(sock.getOutputStream());
			FileInputStream		fStr		= new FileInputStream(file);
			byte[] 				buff 		= new byte[1024];
			int    				rsize;
			int					total = 0;
			String pushCommand = "@@@SSH_KEYS@@@";
			out.write(pushCommand.getBytes(), 0, pushCommand.length());
			while((rsize = fStr.read(buff, 0, 1024)) > 0) {
				//					System.out.println("  rsize = " + rsize);
				out.write(buff, 0, rsize);
				total += rsize;
			}
			fStr.close();
			System.out.println("   size " + total);
			Thread.sleep(3000);
			out.close();
			sock.close();

			System.out.println("  closing Thread");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

