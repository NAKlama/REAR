package de.uni.goettingen.REARClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggingOutput {
	private int		maxSize;
	private boolean enableLogging, logToConsole;

	private BufferedWriter  logFile;

	public void construct(File logFilePath, int maxS, Boolean logToFile, Boolean logToScreen) {
		try {
			maxSize = maxS;
			enableLogging = logToFile;
			logToConsole  = logToScreen;
			File logDir = new File(logFilePath.getParent());
			if(!logDir.exists()) {
				logDir.mkdirs();
			}

			Boolean appendFile = true;
			if(logFilePath.length() > maxSize) {
				appendFile = false;
			}


			logFile = new BufferedWriter(new FileWriter(logFilePath, appendFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public LoggingOutput(File logFilePath) {
		construct(logFilePath, 5000000, true, true);
	}

	public LoggingOutput(File logFilePath, int max, Boolean logToFile, Boolean logToScreen) {
		construct(logFilePath, max, logToFile, logToScreen);
	}

	public void log(String message) {

		// add timestmap to log message
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String msgWithTimestamp = "[" + date.format(new Date()) + "]\t" + message;

		// log to logfile if enabled and logfile is writeable
		if (this.logFile != null && this.enableLogging) {
			try {
				this.logFile.write(msgWithTimestamp);
				this.logFile.newLine();
				this.logFile.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// log to console if enabled
		if (this.logToConsole) {
			System.out.println(msgWithTimestamp);
		}

	}
	
	public void out(String message) {
		log(message);
	}
}
