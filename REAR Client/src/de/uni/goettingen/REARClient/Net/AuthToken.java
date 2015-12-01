package de.uni.goettingen.REARClient.Net;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.security.MessageDigest;

public class AuthToken {
	private static final String			SECRET			= "REPLACE THIS WITH A SECRET STRING!!!";
	private static DateFormat 			dateFormat		= new SimpleDateFormat("yyyyMMddHH");
	private String[] 					date;
	private MessageDigest				sha256;
	
	public AuthToken() {
		date = new String[3];
		Calendar cal = Calendar.getInstance();
		date[0] = dateFormat.format(cal.getTime());
		cal.add(Calendar.HOUR, -1);
		date[1] = dateFormat.format(cal.getTime());
		cal.add(Calendar.HOUR, 2);
		date[2] = dateFormat.format(cal.getTime());
	}



	public Boolean isValid(String t, String command, String salt) {
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
//				System.out.println(date[i]+salt+command + " => " + hashStr.toString());
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
