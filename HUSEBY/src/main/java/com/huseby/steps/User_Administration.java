package com.huseby.steps;


import com.huseby.framework.core.TestContext;
import com.huseby.framework.utils.Utilities;
import com.huseby.pages.HomePage;
import com.huseby.pages.LoginPage;
import com.google.inject.Inject;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.testng.SkipException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class User_Administration {

    @Inject
    TestContext context;
    @Inject
    LoginPage loginPage;
    @Inject
    HomePage homePage;

    protected static final String uuidForApplicationRun = UUID.randomUUID().toString();

    @Given("the user is able to open login page")
    public void theUserIsAbleToOpenLoginPage() {
        context.invokeDriver();
        context.navigateBrowser(Utilities.getSharedProperty("protocol") + "://" + Utilities.getSharedProperty("host"));
    }

    @When("user enters username {string}")
    public void userEntersUsernameHusebyCirruxsolutionsCom(String email) {
        loginPage.enter_email_TextBox(email);
    }

    @And("user enters password {string}")
    public void userEntersPasswordFoobar(String password) {
        loginPage.enter_password_TextBox(password);
    }

    @And("submits the user credentials")
    public void submitsTheUserCredentials() {
         loginPage.click_signin_Button();
    }

    @And("user clicks on the profile settings")
    public void userClicksOnTheProfileSettings() {

    }

    @Then("user is should see username {string}")
    public void userIsShouldSeeUsernameHusebyCirruxsolutionsCom(String email) {
    }

    @And("user open {string} page")
    public void theUserIsAbleToOpenCaseManagementPage(String tabName) {
        switch (tabName) {
            case "schedule":
                homePage.click_schedule_Tab();
                break;

            case "case management":
                homePage.click_casemanagement_Tab();
                break;

            case "users":
                homePage.click_users_Tab();
                break;

            default:
                throw new SkipException("Skipping this test as " + tabName + " button not found");

        }
    }

    @And("user select firm {string} under your cases")
    public void userSelectFirmCirruxSolutionsIncUnderYourCases(String firm) {
        homePage.write_enter_firm_TextBox(firm);
    }

    @And("user click on {string} button")
    public void userClickOnButton(String button) {
        switch (button) {
            case "NEW CASE":
                homePage.click_newcase_Button();
                break;

            case "SAVE":
                homePage.click_save_Button();
                break;

            default:
                throw new SkipException("Skipping this test as " + button + " button not found");

        }
    }

    @And("user selects a case {string}")
    public void userSelectsACaseTestCase(String caseName) {
        caseName = uuidForApplicationRun + caseName;
        homePage.click_select_case(caseName);
    }

    @And("user should fill new case form {string} and {string}")
    public void userShouldFillNewCaseFormCaseNameAndCaseRemarks(String caseName, String caseRemarks) {
        caseName = uuidForApplicationRun + caseName;
        homePage.write_enter_firm_TextBox(caseName, caseRemarks);
    }

    @Then("case {string} should be saved")
    public void caseShouldBeSaved(String caseName) {
        caseName = uuidForApplicationRun + caseName;
        homePage.click_select_case(caseName);
        homePage.verify_Case_Name(caseName);
    }

    @And("user selects {string}{string}")
    public void userSelectsRecordAllYes(String button,String selection) {
        switch (button) {
            case "Record (All):":
                homePage.click_record_All_TextBox_Button(selection);
                break;

            case "Real-time Text (All):":
                homePage.click_realtime_Text_All_Button(selection);
                break;

            case "Exhibit Numbering:":
                homePage.click_exhibit_Numbering_Button(selection);
                break;

            default:
                throw new SkipException("Skipping this test as " + button + " button not found");

        }
    }

    @And("user enter {string} as {string}")
    public void userEnterInternalUserAsKrishnanKrishnanCirruxsolutionsCom(String tabName, String tabValue) {
        switch (tabName) {
            case "Internal User":
                homePage.click_internal_User_TextBox(tabValue);
                break;

            case "Firm Contacts":
                homePage.click_firm_contacts_TextBox(tabValue);
                break;

            case "Participants":
                homePage.click_participants_TextBox(tabValue);
                break;

            default:
                throw new SkipException("Skipping this test as " + tabName + " not found");

        }
    }

    @And("case with case name {string} exist")
    public void caseWithCaseNameTestVsAutomationExist(String caseName) {
        caseName = uuidForApplicationRun + caseName;
        homePage.click_newcase_Button();
        homePage.write_enter_firm_TextBox(caseName, caseName);
        homePage.click_save_Button();
    }

    @Then("user should see validation message indicating {string}")
    public void userShouldSeeValidationMessageIndicatingDuplicateCaseName(String validationError) {
                homePage.verify_required_validation_message(validationError);
    }

    @And("user clear username {string}")
    public void userClearUsernameFoobar(String validationError) {

        loginPage.edit_email_TextBox();
    }
}
