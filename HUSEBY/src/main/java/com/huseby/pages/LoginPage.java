package com.huseby.pages;

import com.huseby.framework.core.TestContext;
import com.huseby.framework.core.WebPageInteract;
import com.google.inject.Inject;
import io.cucumber.guice.ScenarioScoped;
import lombok.extern.log4j.Log4j2;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.SkipException;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@ScenarioScoped
public class LoginPage extends WebPageInteract {
    By email_TextBox = By.name("email");
    By password_TextBox = By.name("password");
    By signin_Button = By.xpath("//button[@type='submit']");


    @Inject
    public LoginPage(TestContext context) {
        super.context = context;
    }
    public void enter_email_TextBox(String input) {writeElement(email_TextBox, input); }
    public void edit_email_TextBox() {editElement(email_TextBox, ""); }
    public void enter_password_TextBox(String input) {writeElement(password_TextBox, input); }
    public void click_signin_Button() {clickElement(signin_Button);}
}
