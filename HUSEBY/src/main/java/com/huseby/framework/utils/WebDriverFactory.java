package com.huseby.framework.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.Assert;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


@Log4j2
public class WebDriverFactory {
    public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static WebDriver createInstance(String browserName, String headless) {
        //To create temp directory
        String parentDirectoryPath = System.getProperty("user.dir");
        String downloadFilepath = parentDirectoryPath + "\\installerDirectory\\";
        File downloadFolder = new File(downloadFilepath);
        if (!downloadFolder.exists()) {
            downloadFolder.mkdir();
        }
        switch (browserName.trim().toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                Map<String, Object> chromePrefs = new HashMap<>();
                chromePrefs.put("profile.default_content_settings.popups", 0);
                chromePrefs.put("download.default_directory", downloadFilepath);
                chromePrefs.put("download.prompt_for_download", false);
                chromePrefs.put("safebrowsing.enabled", true);
                if (Boolean.parseBoolean(headless)) {
                    chromeOptions.addArguments("disable-infobars", "headless", "disable-gpu", "disable-dev-shm-usage",
                            "no-sandbox", "start-maximized");
                    chromeOptions.addArguments("--safebrowsing-disable-download-protection", "safebrowsing-disable-extension-blacklist"
                            ,"--disable-software-rasterizer");
                }
                chromeOptions.setExperimentalOption("prefs", chromePrefs);
                tlDriver.set(new ChromeDriver(chromeOptions));
                break;
            case "firefox":
                FirefoxBinary firefoxBinary = new FirefoxBinary();
                if (Boolean.parseBoolean(headless)) {
                    firefoxBinary.addCommandLineOptions("--headless");
                    firefoxBinary.addCommandLineOptions("--no-sandbox");
                }
                FirefoxProfile profile = new FirefoxProfile();
                FirefoxOptions options = new FirefoxOptions();
                profile.setPreference("browser.download.folderList", 2);
                profile.setPreference("browser.download.manager.showWhenStarting", false);
                profile.setPreference("browser.download.dir", downloadFilepath);
                profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/zip, application/java-archive,text/csv, text/plain,application/octet-stream doc xls pdf txt ,application/x-msdownload");
                WebDriverManager.firefoxdriver().setup();
                options.setBinary(firefoxBinary);
                options.setProfile(profile);
                tlDriver.set(new FirefoxDriver(options));
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (Boolean.parseBoolean(headless)) {
                    edgeOptions.addArguments("disable-infobars", "headless", "disable-gpu", "disable-dev-shm-usage",
                            "no-sandbox", "start-maximized");
                }
                tlDriver.set(new EdgeDriver(edgeOptions));
                break;
            default:
                Assert.fail("Please pass the correct browser name ,supported browser chrome/ff/edge: " + browserName);
        }
        return getDriver();
    }


    public static synchronized WebDriver getDriver() {
        return tlDriver.get();
    }
}
