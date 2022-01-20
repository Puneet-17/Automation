package com.huseby.steps.base;

import com.huseby.framework.core.BaseStep;
import com.huseby.framework.core.TestContext;
import com.google.inject.Inject;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.junit.AssumptionViolatedException;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseStepHooks extends BaseStep {

    @Inject
    TestContext context;

    @Before
    public void before(Scenario scenario) {
        context.setScenario(scenario);

        // Check in Jira to check for open associated tickets
        String messageForOpentickets = "";
        extractTicketsFromScenarioTags(scenario);
        messageForOpentickets = getMessageForOpenTickets();
        if (!messageForOpentickets.isEmpty() || messageForOpentickets.contains("Error")) {
            throw new AssumptionViolatedException(messageForOpentickets);
        }

        setScenario(scenario);
        setup();
    }

    @After(order = 0)
    public void cleanUp() {
        if (context.getDriver() != null) {
            context.quitDriver();
        }
    }

    @After(order = 1)
    public void afterEachStepTakeScreenShot() throws IOException {
        if (context.getScenario().isFailed()) {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String screenshotName = dtf.format(now).replaceAll("/", "").replaceAll(":", "");
            BufferedImage image = new AShot().shootingStrategy(ShootingStrategies.simple())
                    .takeScreenshot(context.getDriver()).getImage();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bout);
            bout.flush();
            byte[] imageInByte = bout.toByteArray();
            bout.close();
            context.getScenario().attach(imageInByte, "image/png", "Failed Screenshot " + screenshotName);
        }
    }
}
