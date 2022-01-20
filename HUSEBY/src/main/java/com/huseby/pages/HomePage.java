package com.huseby.pages;

import com.huseby.framework.core.TestContext;
import com.huseby.framework.core.WebPageInteract;
import com.google.inject.Inject;
import io.cucumber.guice.ScenarioScoped;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.testng.SkipException;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@ScenarioScoped
public class HomePage extends WebPageInteract {
    By schedule_Tab = By.id("simple-tab-schedule");
    By casemanagement_Tab = By.id("simple-tab-cases");
    By users_Tab = By.id("simple-tab-users");
    By firm_TextBox = By.id("combo-box-demo_firms-autocomplete-search");
    By newCase_Button = By.xpath("//span[text()='New Case']");
    By caseName_TextBox = By.id("caseName");
    By caseRemarks_TextBox = By.id("remarks");
    By record_All_Button_Yes = By.xpath("//div[@label='Record (All):']/label[@class='MuiFormControlLabel-root'][1]");
    By record_All_Button_No = By.xpath("//div[@label='Record (All):']/label[@class='MuiFormControlLabel-root'][2]");
    By realtime_Text_All_Button_Yes = By.xpath("//div[@label='Real-time Text (All):']/label[@class='MuiFormControlLabel-root'][1]");
    By realtime_Text_All_Button_No = By.xpath("//div[@label='Real-time Text (All):']/label[@class='MuiFormControlLabel-root'][2]");
    By exhibit_Numbering_Button_Manual = By.xpath("//div[@label='Exhibit Numbering:']/label[@class='MuiFormControlLabel-root'][1]");
    By exhibit_Numbering_Button_Automated = By.xpath("//div[@label='Exhibit Numbering:']/label[@class='MuiFormControlLabel-root'][2]");
    By internal_User_TextBox = By.id("combo-box-demo_1");
    By firm_contacts_TextBox = By.id("combo-box-demo_2");
    By participants_TextBox = By.id("combo-box-demo_3");
    By save_Button = By.xpath("//span[text()='Save']");
    By select_case = By.xpath("//body//input[@placeholder='Search Cases']");
    By find_Case_Name = By.xpath("//input[@id='caseName'][1]");
    By duplicate_Case_Name_message = By.xpath("//div[@class='MuiAlert-message'][1]/text()[1]");
    By scheduleTab_UpcomingEvents = By.xpath("svg[@class='MuiSvgIcon-root']");

    @Inject
    public HomePage(TestContext context) {
        super.context = context;
    }
    public void click_schedule_Tab() {
        clickElement(schedule_Tab);
    }
    public void click_casemanagement_Tab() {
        clickElement(casemanagement_Tab);
    }
    public void click_users_Tab() {
        clickElement(users_Tab);
        sleep(5);
    }
    public void write_enter_firm_TextBox(String firmName) {
        writeElementWithEnter(firm_TextBox, firmName);
    }
    public void write_enter_firm_TextBox(String caseName, String caseRemarks) {
        writeElement(caseName_TextBox, caseName);
        writeElement(caseRemarks_TextBox, caseRemarks);
        sleep(3);
    }
    public void click_record_All_TextBox_Button(String selection) {
        if (selection.contains("No"))
            clickElement(record_All_Button_No);
        else
            clickElement(record_All_Button_Yes);
    }
    public void click_realtime_Text_All_Button(String selection) {
        if (selection.contains("No"))
            clickElement(realtime_Text_All_Button_No);
        else
            clickElement(realtime_Text_All_Button_Yes);
        sleep(2);
    }
    public void click_exhibit_Numbering_Button(String selection) {
        if (selection.contains("Automated"))
            clickElement(exhibit_Numbering_Button_Automated);
        else
            clickElement(exhibit_Numbering_Button_Manual);
        sleep(2);
    }
    public void click_internal_User_TextBox(String tabValue) {
        writeElementWithEnter(internal_User_TextBox, tabValue);
        sleep(1);
    }
    public void click_firm_contacts_TextBox(String tabValue) {
        writeElementWithEnter(firm_contacts_TextBox, tabValue);
        sleep(1);
    }
    public void click_participants_TextBox(String tabValue) {
        writeElementWithEnter(participants_TextBox, tabValue);
        sleep(1);
    }
    public void click_newcase_Button() {clickElement(newCase_Button); sleep(1);}
    public void click_save_Button() {clickElement(save_Button); sleep(10);}
    public void click_select_case(String caseName) {
        writeElementWith_Tab_Enter(select_case, caseName);
        sleep(10);
    }
    public void verify_Case_Name(String caseName) {
        assertThat(getTextByAttribute(find_Case_Name, "value")).isEqualTo(caseName);
    }
    public void verify_required_validation_message(String validationError) {
        switch (validationError) {
            case "duplicate case name":
                String message = "A Case already exists with this name. Please choose a different name.";
                assertThat(getText(duplicate_Case_Name_message)).contains(message);
                break;

            default:
                throw new SkipException("Validation message indicating " + validationError + " not found");

        }
    }
    public void click_scheduleTab_UpcomingEvents () {clickElement(scheduleTab_UpcomingEvents); sleep(5);}

}
