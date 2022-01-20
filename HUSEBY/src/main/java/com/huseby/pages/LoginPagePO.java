package com.huseby.pages;

import com.huseby.framework.core.TestContext;
import com.huseby.framework.core.WebPageInteract;
import com.google.inject.Inject;
import io.cucumber.guice.ScenarioScoped;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;

@Log4j2
@ScenarioScoped
public class LoginPagePO extends WebPageInteract {


    //Login Page
    By emailTextBox = By.id("userEmail");
    By passwordTextBox = By.id("userPassword");
    By loginInButton = By.xpath("//button[contains(@class,'form-action btn')]");

    @Inject
    public LoginPagePO(TestContext context) {
        super.context = context;
    }

    public void enter_Email_TextBox(String input) {
        writeElement(emailTextBox, input);
    }

    public void enter_Password_TextBox(String input) {
        writeElement(passwordTextBox, input);
    }

    public void click_LoginIn_Button() {
        clickElement(loginInButton);
    }

}
