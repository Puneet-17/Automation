package com.huseby.pages;

import com.google.inject.Inject;
import com.huseby.framework.core.WebPageInteract;
import org.openqa.selenium.By;

public class CaseManagement extends WebPageInteract {
    By caseMenu_Button = By.xpath("//button[@id='simple-tab-cases']");
    By newCase_Button = By.xpath("//button[text()='New Case']");

    @Inject
    public void click_caseMenu_Button() {
        clickElement(caseMenu_Button);
    }
    public void click_newCase_Button() {
        clickElement(newCase_Button);
    }
}
