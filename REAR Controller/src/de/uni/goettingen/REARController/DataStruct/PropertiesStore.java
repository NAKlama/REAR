package de.uni.goettingen.REARController.DataStruct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

//import org.apache.commons.lang3.SystemUtils;

import de.uni.goettingen.REARController.MainWindow;

public class PropertiesStore {
	private static final File 	PROP_FILE	= new File("REAR_Controller.conf");
	
//	private static final String	DEFAULT_UPLOAD_SERVER			= "134.76.187.188";
	private static final String	DEFAULT_UPLOAD_SERVER			= "134.76.185.241";
	private static final String	DEFAULT_UPLOAD_USER				= "REAR";

	Properties	prop;
	
	public PropertiesStore() {
//		windows = SystemUtils.IS_OS_WINDOWS;
//		unix	= SystemUtils.IS_OS_UNIX;
//		if(!windows && !unix) {
//			// No clue what OS we are using
//			// TODO do something here
//		}
		
		prop = new Properties();
//		if(windows)
//			prop.setProperty("tmpDir", DEFAULT_TEMP_DIR_WIN);
//		else if(unix)
//			prop.setProperty("tmpDir", DEFAULT_TEMP_DIR_LINUX);
		prop.setProperty("serverAddr", DEFAULT_UPLOAD_SERVER);
		prop.setProperty("serverUser", DEFAULT_UPLOAD_USER);
		
		load();
	}
	
	public void load() {
		if(PROP_FILE.exists() && PROP_FILE.isFile() && PROP_FILE.canRead()) {
			try {
				FileInputStream fis = new FileInputStream(PROP_FILE);
				prop.load(fis);
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream(PROP_FILE);
			prop.store(fos, "REAR Controller " + MainWindow.getVersion());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setUploadServer(String addr) {
		prop.setProperty("serverAddr", addr);
		save();
	}
	
	public void setUploadUser(String user) {
		prop.setProperty("serverUser", user);
		save();
	}
	
	public void setDebugMode(Boolean dMode) {
		String modeStr = new String();
		if(dMode)
			modeStr = "true";
		else
			modeStr = "false";
		prop.setProperty("debugMode", modeStr);
		save();
	}
	
	public String getUploadServer() {
		return prop.getProperty("serverAddr");
	}
	
	public String getUploadUser() {
		return prop.getProperty("serverUser");
	}
	
	public Boolean getDebugMode() {
		String modeStr = prop.getProperty("debugMode");
		if(modeStr != null && modeStr.equals("true"))
			return true;
		return false;
	}
}
