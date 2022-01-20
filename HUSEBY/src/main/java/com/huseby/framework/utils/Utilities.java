package com.huseby.framework.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class Utilities {
	private static final Logger logger = LoggerFactory.getLogger(Utilities.class);
	/**
	 * Generate random number
	 *
	 * @param aStart
	 *            : start length of random integer
	 * @param aEnd
	 *            : end length of random number like 10-11 it will generate
	 *            random number between 10-11 digits
	 * @return
	 */
	public static int getRandomInteger(int aStart, int aEnd) {
		Random aRandom = new Random();
		if (aStart > aEnd) {
			throw new IllegalArgumentException("Start cannot exceed End.");
		}
		// get the range, casting to long to avoid overflow problems
		long range = (long) aEnd - (long) aStart + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long) (range * aRandom.nextDouble());
		return (int) (fraction + aStart);
	}

	/**
	 * Generate random string
	 *
	 * @param len
	 *            : length of random string
	 * @return
	 */
	public static String randomString(int len) {
		String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}

	/**
	 * Get absolute path
	 */
	public static String getPath() {
		String path = "";
		File file = new File("");
		String absolutePathOfFirstFile = file.getAbsolutePath();
		path = absolutePathOfFirstFile.replaceAll("\\\\+", "/");
		return path;
	}

	/**
	 * @param file
	 * @return
	 */
	public static String getFileName(String file) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		Calendar cal = Calendar.getInstance();
		String fileName = file + dateFormat.format(cal.getTime());
		return fileName;
	}

	/**
	 * Get absolute path
	 */
	public static String getPathUpload() {
		String path = "";
		File file = new File("");
		String absolutePathOfFirstFile = file.getAbsolutePath();
		path = absolutePathOfFirstFile.replaceAll("/", "//");
		return path;
	}

	/**
	 * Get time stamp
	 *
	 * @return
	 */
	public static long getTimeStamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.getTime();
	}

	/**
	 * Get time stamp
	 *
	 * @return
	 * @throws Exception
	 */
	public static String[] splitStringWithNewLine(String str) throws Exception {
		if (StringUtils.isBlank(str)) {
			throw new Exception("Please provide valid string");
		}
		return str.split("\\r?\\n");
	}

	/**
	 * Convert string with first letter in caps
	 *
	 * @param inputString
	 * @return
	 */
	public static String titleCaseConversion(String inputString) {
		if (StringUtils.isBlank(inputString)) {
			return "";
		}

		if (StringUtils.length(inputString) == 1) {
			return inputString.toUpperCase();
		}

		StringBuffer resultPlaceHolder = new StringBuffer(inputString.length());

		Stream.of(inputString.split(" ")).forEach(stringPart -> {
			if (stringPart.length() > 1)
				resultPlaceHolder.append(stringPart.substring(0, 1).toUpperCase())
						.append(stringPart.substring(1).toLowerCase());
			else
				resultPlaceHolder.append(stringPart.toUpperCase());

			resultPlaceHolder.append(" ");
		});
		return StringUtils.trim(resultPlaceHolder.toString());
	}

	public static String getCharactersFromString(String data){
		String resultString = data.replaceAll("\\P{L}", "");
		return resultString;
	}
	public static String removeNumberFromString(String data){
		return  data.replaceAll("\\d","");
	}


	public static String removeText(String text, String trim) {
		return trim.replace(text, "").trim();
	}

	public static String removeLine(String str) {
		String updatedString = StringUtils.EMPTY;
		String[] lines = str.split("\n");
		for(String str1 : lines) {
			updatedString = updatedString + " "+str1;
		}
		return updatedString.trim();
	}

	public static void renameCucumberReportFolder() throws IOException {
		String applicationName = System.getProperty("applicationName");
		String actionType = System.getProperty("actionType");
		String agentOS = System.getProperty("agentOS");
		String serverOS = System.getProperty("serverOS");

		String sourceDirectoryLocation = System.getProperty("user.dir")+"/target/cucumber-html-report/";
		String destinationDirectoryLocation = System.getProperty("user.dir")+"/target/"+applicationName + "-" + actionType + "-" + agentOS + "-" + serverOS;

		Files.walk(Paths.get(sourceDirectoryLocation))
				.forEach(source -> {
					Path destination = Paths.get(destinationDirectoryLocation, source.toString()
							.substring(sourceDirectoryLocation.length()));
					try {
						Files.copy(source, destination);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}

//	public static void writeJSONComparisonLog(HashMap<String, String> errorDetails) throws IOException {
//		String applicationName = System.getProperty("applicationName");
//		String actionType = System.getProperty("actionType");
//		String agentOS = System.getProperty("agentOS");
//		String serverOS = System.getProperty("serverOS");
//
//		Iterator<Map.Entry<String, AbstractMap.SimpleEntry<String, Integer>>> it = errorDetails.entrySet().iterator();
//		// iterating every set of entry in the HashMap.
//		BufferedWriter writer = new BufferedWriter(new FileWriter( System.getProperty("user.dir")+"/output/"+applicationName + "-" + actionType + "-" + agentOS + "-" + serverOS+"/Comparison.log"));
//		while (it.hasNext()) {
//			Map.Entry<String, AbstractMap.SimpleEntry<String, Integer>> set = (Map.Entry<String, AbstractMap.SimpleEntry<String, Integer>>) it.next();
//			writer.write("Reference nodes - " + set.getValue().getKey() +"\n");
//			writer.write("Error Count/Message - " + set.getValue().getValue() + " / " + set.getKey() + "\n\n");
//		}
//	}

	public static void updateCucumberFile() throws IOException, ParseException {
		String applicationName = Utilities.getSharedProperty("applicationName").toString();
		String actionType = Utilities.getSharedProperty("actionType").toString();
		String agentOS = Utilities.getSharedProperty("agentOS").toString();
		String serverOS = Utilities.getSharedProperty("serverOS").toString();
		String agentBrowser = Utilities.getSharedProperty("agentBrowser").toString();

 		String file = System.getProperty("user.dir")+"/target/"+applicationName + "-" + actionType + "-" + agentOS + "-" + serverOS + ".json";

		logger.trace("Inside " + (new Throwable()).getStackTrace()[0].getMethodName());

		JSONObject serverObject = new JSONObject();
		JSONObject server = new JSONObject();
		JSONObject agentObject = new JSONObject();
		JSONObject agent = new JSONObject();
		JSONObject applicationObject = new JSONObject();
		JSONObject application = new JSONObject();
		JSONObject actionObject = new JSONObject();
		JSONObject action = new JSONObject();
		JSONObject browserObject = new JSONObject();
		JSONObject browser = new JSONObject();

		serverObject.put("name","Server OS");
		serverObject.put("value",serverOS);
		server.putAll(serverObject);
		agentObject.put("name","Agent OS");
		agentObject.put("value",agentOS);
		agent.putAll(agentObject);
		applicationObject.put("name","Application");
		applicationObject.put("value",applicationName);
		application.putAll(applicationObject);
		actionObject.put("name","Action");
		actionObject.put("value",actionType);
		action.putAll(actionObject);
		browserObject.put("name","Browser");
		browserObject.put("value",agentBrowser);
		browser.putAll(browserObject);


		JSONParser parser = new JSONParser();
		ObjectMapper mapper = new ObjectMapper();

		Object obj = parser.parse(new FileReader(file));

		if (((JSONArray)obj).size()==0)
		{
			String templateFile = System.getProperty("user.dir")+"/src/main/resources/multi-Cucumber-template.json";
			obj = parser.parse(new FileReader(templateFile));
		}
		JsonNode jsonNode = mapper.readTree(((JSONArray) obj).toJSONString());
		JsonNode elem0 = ((ArrayNode) jsonNode).get(0);
		((ObjectNode) elem0).putArray("metadata")
				.insertPOJO(0, server)
				.insertPOJO(1, agent)
				.insertPOJO(2, application)
				.insertPOJO(3, action)
				.insertPOJO(3, browser);
		String json = elem0.toString();
		((ArrayNode) jsonNode).set(0,elem0);
		logger.debug(json);

		file = System.getProperty("user.dir")+"/target/MultiReport-"+applicationName + "-" + actionType + "-" + agentOS + "-" + serverOS + "-" + agentBrowser + ".json";
		FileWriter writer = new FileWriter(file);
		writer.write(jsonNode.toString().replace("CUCUMBER_TAG", getSharedProperty("cucumber.filter.tags").toString()));
		writer.flush();

		logger.trace("Exiting " + (new Throwable()).getStackTrace()[0].getMethodName());
	}
	
	// ***  It will create the output directories and subdirectories to store the failed dependency nodes ***
	public static void createOutputDirectories() throws IOException {
		String applicationName = getSharedProperty("applicationName").toString();
		String actionType = getSharedProperty("actionType").toString();
		String agentOS = getSharedProperty("agentOS").toString();
		String serverOS = getSharedProperty("serverOS").toString();

		String dirPath = getSharedProperty("user.dir").toString() + "/output/" + applicationName
				+ "-" + actionType + "-" + agentOS + "-" + serverOS;
		File folder = FileUtils.getFile(dirPath);

		if (!folder.exists()) {
			FileUtils.forceMkdir(folder);

			if (!folder.exists()) {
				Assert.fail("Folder not created: " + dirPath);
			}
			System.out.println("Folder created: " + dirPath + "  " + folder.exists());
		}
	}
	private static HashMap<String, Object> propertyBag = new HashMap<String, Object>();

	public static Object getSharedProperty(String name) {
		return propertyBag.get(name);
	}

	public static void setSharedProperty(String name, Object value) {
		propertyBag.put(name, value);
	}

	public static void readPropertyToBag() {
		Map<String, String> env = System.getenv();
		for (String key : env.keySet()) {
			String value = env.get(key);
			setSharedProperty(key,value);
		}

		final Properties systemProperties = System.getProperties();
		final Set<String> keys = systemProperties.stringPropertyNames();
		for (final String key : keys) {
			final String value = systemProperties.getProperty(key);
			setSharedProperty(key,value);
		}
	}

	public static void writeJSONComparisonLog(HashMap<String, AbstractMap.SimpleEntry<String, Integer>> errorDetails) throws IOException {
		String applicationName = Utilities.getSharedProperty("applicationName").toString();
		String actionType = Utilities.getSharedProperty("actionType").toString();
		String agentOS = Utilities.getSharedProperty("agentOS").toString();
		String serverOS = Utilities.getSharedProperty("serverOS").toString();

		Iterator<Map.Entry<String, AbstractMap.SimpleEntry<String, Integer>>> it = errorDetails.entrySet().iterator();
		// iterating every set of entry in the HashMap.
		BufferedWriter writer = new BufferedWriter(new FileWriter( System.getProperty("user.dir")+"/output/"+applicationName + "-" + actionType + "-" + agentOS + "-" + serverOS+"/Comparison.log"));
		while (it.hasNext()) {
			Map.Entry<String, AbstractMap.SimpleEntry<String, Integer>> set = (Map.Entry<String, AbstractMap.SimpleEntry<String, Integer>>) it.next();
			writer.write("Reference nodes - " + set.getValue().getKey() +"\n");
			writer.write("Error Count/Message - " + set.getValue().getValue() + " / " + set.getKey() + "\n\n");
		}
	}


}
