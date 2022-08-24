package de.uni.goettingen.REARClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class PropertiesStore {
	public static final String	DEFAULT_PASSPHRASE	= "B2WX1bXaxSJ4g4E8N6zULPXBOjdFXb5fZhh0BjFx7mhUgpzig5Pi5HKEhTF1a1tC";
	public static final String	CONFIG_FILENAME		= "REARclient.conf";
	public static final String	DEFAULT_PATH		= "C:\\Users\\ilias\\REAR\\";
	
	public static final String	DEFAULT_CONF_PATH	= DEFAULT_PATH + CONFIG_FILENAME;
	public static final String	DEFAULT_AUDIO_PATH	= DEFAULT_PATH + "audio\\";
	public static final String	DEFAULT_FILE		= DEFAULT_AUDIO_PATH + "default.flac";
	
	private static final int	DEFAULT_TCP_PORT			= 15000;	
	public  static final int	DEFAULT_DATA_PORT			= 28947;
	public  static final String	DEFAULT_UPLOAD_SERVER		= "134.76.185.241";
//	public  static final String	DEFAULT_UPLOAD_SERVER		= "134.76.187.188";
//	public  static final String	DEFAULT_UPLOAD_SERVER		= "192.168.246.132";
	public  static final String	DEFAULT_UPLOAD_SERVER_USER	= "REAR";
	
	public  static final String DEFAULT_UPLOAD_PATH			= "S:\\TestDAF\\flac\\";
	
	public String DEFAULT_LOGGING_FILE;
	
	private Properties prop;
	
	public PropertiesStore() {
		prop = new Properties();
		DEFAULT_LOGGING_FILE = System.getProperty("user.home") + File.separator
		        + "AppData"  + File.separator
		        +  "Roaming"  + File.separator
		        + "SafeExamBrowser" + File.separator
		        + "ExamScreenShotTool.log";
		
		
	}
	
	public void load(ArrayList<File> dirList) {
		for(File d : dirList) {
			File confFile = new File(d, CONFIG_FILENAME);
			if(confFile.exists() && confFile.isFile() && confFile.canRead()) {
				Boolean success = true;
				try {
					FileInputStream fis = new FileInputStream(confFile);
					prop.load(fis);
					fis.close();
					prop.setProperty("PROP_FILE", confFile.getAbsolutePath());
				} catch (IOException e) {
					success = false;
				}
				if(success) break;
			}
		}
	}
	
	public void save(File f) {
		prop.setProperty("SSH_Passphrase", this.getSSHPassphrase());
		prop.setProperty("DefaultPath", this.getDefaultPath());
		prop.setProperty("AudioPath", this.getAudioPath());
		prop.setProperty("AudioFile", this.getDefaultAudioFile());
		prop.setProperty("UploadServer", this.getUploadServer());
		prop.setProperty("UploadServerUser", this.getUploadServerUser());
		prop.setProperty("UploadPath", this.getUploadPath());
		prop.setProperty("LogFile", this.getLogFile());
		
		prop.setProperty("ListenPort", String.valueOf(this.getListenPort()));
		prop.setProperty("DataPort", String.valueOf(this.getDataPort()));
		
		prop.setProperty("PROP_FILE", f.getAbsolutePath());
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			prop.store(fos, "REAR Client");
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void save() {
		File   confFile = null;
		String confPath = prop.getProperty("PROP_FILE");
		if(confPath != null) {
			confFile = new File(confPath);
		} else {
			confFile = new File(DEFAULT_CONF_PATH);
		}
		this.save(confFile);
	}
	
	public String getSSHPassphrase() {
		return prop.getProperty("SSH_Passphrase", DEFAULT_PASSPHRASE);
	}
	
	public String getDefaultPath() {
		return prop.getProperty("DefaultPath", DEFAULT_PATH);
	}
	
	public String getAudioPath() {
		return prop.getProperty("AudioPath", DEFAULT_AUDIO_PATH);
	}
	
	public String getDefaultAudioFile() {
		return prop.getProperty("AudioFile", DEFAULT_FILE);
	}
	
	public int getListenPort() {
		String portStr = prop.getProperty("ListenPort");
		if(portStr == null)
			return DEFAULT_TCP_PORT;
		return Integer.parseInt(portStr);
	}
	
	public int getDataPort() {
		String portStr = prop.getProperty("DataPort");
		if(portStr == null)
			return DEFAULT_DATA_PORT;
		return Integer.parseInt(portStr);
	}
	
	public String getUploadServer() {
		return prop.getProperty("UploadServer", DEFAULT_UPLOAD_SERVER);
	}
	
	public String getUploadServerUser() {
		return prop.getProperty("UploadServerUser", DEFAULT_UPLOAD_SERVER_USER);
	}
	
	public String getUploadPath() {
		return prop.getProperty("UploadPath", DEFAULT_UPLOAD_PATH);
	}
	
	public String getLogFile() {
		return prop.getProperty("LogFile", DEFAULT_LOGGING_FILE);
	}
}
