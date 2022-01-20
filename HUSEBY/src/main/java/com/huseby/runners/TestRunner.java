package com.huseby.runners;

import java.io.IOException;
import java.util.stream.Stream;

import ch.qos.logback.classic.Level;
import com.huseby.framework.ExceptionAutomationFailure;
import org.json.simple.parser.ParseException;

import com.huseby.framework.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TestRunner {
	private static final Logger logger = LoggerFactory.getLogger(Utilities.class);



	public static void main(String[] args) throws IOException, ParseException, ExceptionAutomationFailure {
		Utilities.readPropertyToBag();
		Utilities.createOutputDirectories();
		((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.valueOf(Utilities.getSharedProperty("logLevel").toString()));

		String[] defaultOptions = {
				"classpath:features",
				"--glue", "com.huseby.steps",
				"--tags", Utilities.getSharedProperty("cucumber.filter.tags").toString(),
				"--plugin", "pretty",
				"--plugin", "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
				"--plugin", "json:target/" + Utilities.getSharedProperty("applicationName") + "-" + Utilities.getSharedProperty("actionType") + "-" + Utilities.getSharedProperty("agentOS") + "-" + Utilities.getSharedProperty("serverOS") + ".json",
				"--plugin", "timeline:target/cucumber-html-report",
				"--threads", Utilities.getSharedProperty("threads").toString()
		};
		logger.info("Start cucumber automation run");
		Stream<String> cucumberOptions = Stream.concat(Stream.of(defaultOptions), Stream.of(args));
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		io.cucumber.core.cli.Main.run(cucumberOptions.toArray(String[]::new), contextClassLoader);

		Utilities.updateCucumberFile();
	}
}