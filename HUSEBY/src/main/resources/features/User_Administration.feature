Feature: Check APP Info UI under administration

  @Login1
  Scenario: Verify user should should be able to login to the web application
    Given the user is able to open login page
    When user enters username 'huseby@cirruxsolutions.com'
    And user enters password 'foobar'
    And submits the user credentials
    And user clicks on the profile settings
    Then user is should see username 'huseby@cirruxsolutions.com'
  @ScheduleTabUpcomingEventList @Login1
  Scenario: Verify user is able to see upcoming events list on schedule tab
    Given the user is able to open login page
    And user enters username 'huseby@cirruxsolutions.com'
    And user enters password 'foobar'
    And submits the user credentials
    When user open 'schedule' page
    And user clicks on upcoming events list
    Then user should see list of upcoming events list
  @CreateNewCase @Login
  Scenario: Verify user is able to create a new case
    Given the user is able to open login page
    And user enters username 'huseby@cirruxsolutions.com'
    And user enters password 'foobar'
    And user clear username 'foobar'
    And submits the user credentials
    When user open 'case management' page
    And user select firm 'Cirrux Solutions, Inc.' under your cases
    And user click on 'NEW CASE' button
    And user should fill new case form 'Test vs Automation' and 'caseRemarks'
    And user selects 'Record (All):''No'
    And user selects 'Real-time Text (All):''No'
    And user selects 'Exhibit Numbering:''Manual'
    And user enter 'Internal User' as 'krishnan 5 (krishnan5@cirruxsolutions.com)'
    And user enter 'Firm Contacts' as 'Krishnan1 Govind1 (krishnan1@cirruxsolutions.com)'
    And user enter 'Participants' as 'Krishnan G (krishnan@sqaworx.com)'
    And user click on 'SAVE' button
    Then case 'Test vs Automation' should be saved
  @CreateNewCase @Login1
  Scenario: Verify user should see validation message if duplicate case name entered
    Given the user is able to open login page
    And user enters username 'huseby@cirruxsolutions.com'
    And user enters password 'foobar'
    And submits the user credentials
    And user open 'case management' page
    And user select firm 'Cirrux Solutions, Inc.' under your cases
    #And case with case name 'Test vs Automation' exist
    And user click on 'NEW CASE' button
    And user should fill new case form 'Test vs Automation' and 'caseRemarks'
    And user click on 'SAVE' button
    Then user should see validation message indicating 'duplicate case name'
  @UpdateCase @Login1
  Scenario: Verify user is able to update case name of an existing case
    Given the user is able to open login page
    And user enters username 'huseby@cirruxsolutions.com'
    And user enters password 'foobar'
    And submits the user credentials
    When user open 'casemanagement' page
    And user select firm 'Cirrux Solutions, Inc.' under your cases
    And user selects a case 'Test Case vs New Case'
    And user updates case name
    And click save button
    Then case name should be updated
