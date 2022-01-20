package com.huseby.framework.core;

import com.huseby.framework.utils.Utilities;
import com.huseby.framework.utils.WebDriverFactory;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.Scenario;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

@Log4j2
@Data
@ScenarioScoped
public class TestContext extends BaseCore {
    WebDriver driver;

    @Setter
    @Getter
    Scenario scenario;

    @Getter
    CoreConfig coreConfig = ConfigFactory.create(CoreConfig.class);

    public void invokeDriver() {
        this.driver = WebDriverFactory.createInstance(Utilities.getSharedProperty("agentBrowser").toString(), Utilities.getSharedProperty("headless").toString());
        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(coreConfig.browserImplicitWaitTimeOut()));
        this.driver.manage().window().maximize();
        reportLog(Utilities.getSharedProperty("agentBrowser") + " browser Opened.", LogType.trace);
    }

    public void navigateBrowser(String url) {
        this.driver.get(url);
        reportLog("browser navigated: " + url,LogType.trace);
    }

    public void quitDriver() {
        this.driver.quit();
        reportLog("Driver quit success",LogType.trace);
    }

}
