package de.uni.goettingen.REARController.Net;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthToken {
	private static final String			SECRET			= "REPLACE THIS WITH A SECRET STRING!!!";
	private static final DateFormat		dateFormat		= new SimpleDateFormat("yyyyMMddHH");
	private String[] 					date;
	private MessageDigest				sha256;
	private Date						lastUpdate;
	
	public AuthToken() {
		date = new String[3];
		update();
	}

	private void update() {
		Calendar cal = Calendar.getInstance();
		date[0] = dateFormat.format(cal.getTime());
		cal.add(Calendar.HOUR, -1);
		date[1] = dateFormat.format(cal.getTime());
		cal.add(Calendar.HOUR, 2);
		date[2] = dateFormat.format(cal.getTime());
		lastUpdate = new Date();
	}
	
	public String getToken(String command, String salt) {
		Date now = new Date();
		if(now.getTime() - lastUpdate.getTime() > 300000)
			update();
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
			sha256.update(date[1].getBytes(), 0, date[1].length());
			sha256.update(salt.getBytes(), 0, salt.length());
			sha256.update(command.getBytes(), 0, command.length());
			sha256.update(SECRET.getBytes(), 0, SECRET.length());
			byte[] hash = sha256.digest();
			StringBuilder hashStr = new StringBuilder();
			for(byte b : hash)
				hashStr.append(String.format("%02X", b));
			return hashStr.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Boolean isValid(String t, String command, String salt) {
		Date now = new Date();
		if(now.getTime() - lastUpdate.getTime() > 300000)
			update();
		try {
			for(int i=0; i<3; ++i) {
				sha256 = MessageDigest.getInstance("SHA-256");
				sha256.update(date[i].getBytes(), 0, date[i].length());
				sha256.update(salt.getBytes(), 0, salt.length());
				sha256.update(command.getBytes(), 0, command.length());
				sha256.update(SECRET.getBytes(), 0, SECRET.length());

				byte[] hash = sha256.digest();
				StringBuilder hashStr = new StringBuilder();
				for(byte b : hash)
					hashStr.append(String.format("%02X", b));
				if(hashStr.toString().equals(t))
					return true;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
