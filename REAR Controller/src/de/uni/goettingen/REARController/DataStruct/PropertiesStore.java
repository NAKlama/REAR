package de.uni.goettingen.REARController.DataStruct;

import java.io.File;
import java.util.Properties;

public class PropertiesStore {
	public static final String	DEFAULT_UPLOAD_SERVER			= "134.76.187.188";
	public static final String	DETAULT_UPLOAD_USER				= "REAR";
	public static final File	DEFAULT_TEMP_DIR				= new File("C:\\tmp");
	
	Properties	prop;
	
	public PropertiesStore() {
		prop = new Properties();
	}
}
