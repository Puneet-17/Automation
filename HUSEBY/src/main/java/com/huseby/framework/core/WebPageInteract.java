package com.huseby.framework.core;

import lombok.extern.log4j.Log4j2;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class WebPageInteract extends BaseCore {


    protected TestContext context;
    private final long timeOutInSeconds;
    CoreConfig config = ConfigFactory.create(CoreConfig.class);

    public WebPageInteract() {
        timeOutInSeconds = config.browserExplicitWaitTimeOut();
    }


    public void clickElement(By by) {
        scrollIntoView(by);
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(timeOutInSeconds));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
        element.click();
        reportLog("Element is clicked. Element Description: " + by.toString(),LogType.trace);
    }

    public WebElement writeElement(By by, String text) {
        scrollIntoView(by);
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(timeOutInSeconds));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
        element.sendKeys(text);
        reportLog("Element is Set with text as: " + text + ". Element Description: " + by.toString(),LogType.trace);
        return element;
    }

    public WebElement editElement(By by, String text) {
        scrollIntoView(by);
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(timeOutInSeconds));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
        element.clear();
        element.sendKeys(text);
        reportLog("Element is Set with text as: " + text + ". Element Description: " + by.toString(),LogType.trace);
        return element;
    }

    public WebElement writeElementWithEnter(By by, String text) {
        scrollIntoView(by);
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(timeOutInSeconds));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
        element.sendKeys(text);
        sleep(5);
        element.sendKeys(Keys.ARROW_DOWN,Keys.RETURN);
        reportLog("Element is Set with text as: " + text + ". Element Description: " + by.toString(),LogType.trace);
        return element;
    }

    public WebElement writeElementWith_Tab_Enter(By by, String text) {
        scrollIntoView(by);
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(timeOutInSeconds));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
        element.sendKeys(text);
        sleep(5);
        element.sendKeys(Keys.TAB, Keys.ENTER);
        sleep(2);
        reportLog("Element is Set with text as: " + text + ". Element Description: " + by.toString(),LogType.trace);
        return element;
    }

    public String getTextByAttribute(By by, String attName) {
        scrollIntoView(by);
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(timeOutInSeconds));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        String value = element.getAttribute(attName);
        reportLog("Get Attribute for element: " + by.toString() + " Attribute name: " + attName,LogType.trace);
        return value;
    }

    public boolean isAttributePresent(By by, String attName) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean result = false;
        try {
            String value = context.getDriver().findElement(by).getAttribute(attName);
            if (value == null) {
                result = true;
            }
        } catch (Exception ignored) {
        }
        reportLog("Present Attribute for element: " + by.toString() + " Attribute name: " + attName,LogType.trace);
        return result;
    }

    public String getText(By by) {
        scrollIntoView(by);
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(timeOutInSeconds));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
        String value = element.getText();
        reportLog("Get Text for element: " + by.toString() + " Text : " + value,LogType.trace);
        return value;
    }

    public String getTitle() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String value = context.getDriver().getTitle();
        reportLog("Title fetched: " + value,LogType.trace);
        return value;
    }

    public List<WebElement> getListOfWebElements(By by) {
        scrollIntoView(by);
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(timeOutInSeconds));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        reportLog("List of Elements returned for description: " + by.toString(),LogType.trace);
        return element.findElements(by);
    }

    public List<String> getListOfValueOfWebElements(By bys) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        scrollIntoView(bys);
        List<WebElement> elementList = context.getDriver().findElements(bys);
        for (WebElement element : elementList) {
            stringArrayList.add(element.getText());
        }
        reportLog("List of text returned for WebElement: " + stringArrayList,LogType.trace);
        return stringArrayList;
    }

    public boolean validateElementIsDisplayed(By by) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean b = true;
        try {
            context.getDriver().findElement(by).isDisplayed();
        } catch (Exception e) {
            b = false;
        }
        reportLog("Element is Displayed status: " + b,LogType.trace);
        return b;
    }

    public byte[] takeScreenElementShot(By by) {
        TakesScreenshot shot = context.getDriver().findElement(by);
        reportLog("Screen Shot taken for element: " + by.toString(),LogType.trace);
        return shot.getScreenshotAs(OutputType.BYTES);
    }

    public String getWebElementBackgroundStyleAttributeAfterUsingLinkText(By by) {
        WebElement webElement = context.getDriver().findElement(by);
        String s;
        s = Color.fromString(((JavascriptExecutor) context.getDriver())
                .executeScript("return window.getComputedStyle(arguments[0], ':after')" + ".getPropertyValue('background-color');", webElement)
                .toString()).asHex().toUpperCase();
        reportLog("Style for flex Element: " + by.toString() + " Attribute name: " + s,LogType.trace);
        return s;
    }

    public String getWebElementBackgroundStyleAttributeAfterUsingXpath(By by) {
        WebElement webElement = context.getDriver().findElement(by);
        reportLog("Style for Element: " + by.toString() + " Background Style: " + Color.fromString(webElement.getCssValue("background-color")).asHex().toUpperCase(),LogType.trace);
        return Color.fromString(webElement.getCssValue("background-color")).asHex().toUpperCase();
    }

    public String getWebElementFontFamilyAttributeAfterUsingXpath(By by) {
        WebElement webElement = context.getDriver().findElement(by);
        reportLog("Style for Element: " + by.toString() + " Style name: " + webElement.getCssValue("font-family"),LogType.trace);
        return webElement.getCssValue("font-family");
    }

    public void scrollIntoView(By by) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ((JavascriptExecutor) context.getDriver()).executeScript("arguments[0].scrollIntoView({block: \"center\",inline: \"center\",behavior: \"smooth\"});", context.getDriver().findElement(by));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void scrollIntoViewByEle(WebElement by) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ((JavascriptExecutor) context.getDriver()).executeScript("arguments[0].scrollIntoView({block: \"center\",inline: \"center\",behavior: \"smooth\"});", by);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}