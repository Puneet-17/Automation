package com.huseby.framework.core;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonFileUtil {

	/**
	 * read json file content from file
	 * 
	 * @param fileName:
	 *            it contains file name
	 * @param input:
	 *            set flag value for input of output file
	 * @return : it will return JSON object of content
	 * @throws Exception
	 */
	public static Object readJsonData(String folderName, String fileName) throws Exception {
		if (fileName.isEmpty() || fileName == null)
			throw new Exception("Please provide valid file name");
		JSONParser parser = new JSONParser();
		Object jsonObject = null;
		
		try {
			jsonObject = parser.parse(new FileReader(folderName + "/" + fileName + ".json"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	
}