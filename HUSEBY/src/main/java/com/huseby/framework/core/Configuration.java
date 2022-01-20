package com.huseby.framework.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;


public class Configuration {
  
	@SuppressWarnings("resource")
	public static String getConfig(String key) throws Exception {
        String value;
        try {
            Properties prop = new Properties();        
            File f = new File(System.getProperty("user.dir") + "/config.properties");
            if (f.exists()) {
                prop.load(new FileInputStream(f));
                value = prop.getProperty(key);
            } else {
                throw new Exception("File config.properties not found under location root location of project");
            }
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Failed to read data from application.properties file.");
            throw ex;
        }
        if (value == null) {
            throw new Exception("Key "+key+" not found in config properties file");
        }
        return value;
    }
   
}
