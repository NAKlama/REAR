package de.uni.goettingen.REARController.Net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

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
		ssh = null;
	}
	
	public void push(String user, String server, File f, String to) {
		file = f;
		if(ssh == null)
			try {
				ssh	= jsch.getSession(user, server, 22);
				UserInfo	ui		= new SSHUserInfo();
				ssh.setUserInfo(ui);
				ssh.setConfig("StrictHostKeyChecking", "no");
				ssh.setPortForwardingL(28947, "127.0.0.1", 28947);
				ssh.connect();
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		try {
			Socket 				sock		= new Socket("127.0.0.1", 28947);
			DataOutputStream	out			= new DataOutputStream(sock.getOutputStream());
			FileInputStream		fStr		= new FileInputStream(file);
			byte[] 				buff 		= new byte[1024];
			int    				rsize;
//			int					total = 0;
//			System.out.println("Thread started");
			String pushCommand = "@@@SSH_KEYS@@@\n";
			out.write(pushCommand.getBytes(), 0, pushCommand.length());
			while((rsize = fStr.read(buff, 0, 1024)) > 0) {
				System.out.println("  rsize = " + rsize);
				out.write(buff, 0, rsize);
//				total += rsize;
			}
			fStr.close();
//			System.out.println("   size " + total);
			Thread.sleep(3000);
			out.close();
			sock.close();
			ssh.disconnect();

//			System.out.println("  closing Thread");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

