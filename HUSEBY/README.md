# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* This repository contains selenium automation code base of crosscode application
* Version
* [Framework Details and Setup Guideline document ](https://crosscode.atlassian.net/wiki/spaces/UAT/pages/1139900420/Automation+Testing+Framework )

### How do I get set up? ###

* Install Java JDK and setup Java path in System Environment variable
* Download and setup Apache Maven path in System Environment variable

### Code Setup ###
* Clone automation code in your local drive from this repository

### How to run tests ###
* Directly on a host:
  * Open command prompt and goto root directory of 
  * Run below command 
  * mvn clean install -Dsuite=suite
* Using Docker
  * Change directory to the `docker` subfolder in the project
  * `./run_tests.sh`

### How to execute installation tests
* Setup environment values path for below
    * host
    * TARGET_VM_USER
    * TARGET_VM_PASSWORD
    * TARGET_VM_PORT
    * TARGET_VM_ENS
    * TARGET_AGENT_IP
    * TARGET_AGENT_USER
    * TARGET_AGENT_PASSWORD
    * TARGET_AGENT_PORT

* Note: by default Agent configuration will set for Server VM details if we are not passing values for Agent. If we want to run on Agent on separate 
* environment then we need to provide Agent environment details, along with host ( huseby server IP address )

* Open command prompt and go to automation project root directory and run commands:
    * ---------------------------------
    * create jar file by using below command:
    * mvn clean package assembly:single
    * -----------------------------
    * executed test by command:
    * java -jar -Dcucumber.filter.tags=@ServerOverSSH target/huseby.jar
    * --------------------------------- 
    * where installation tag names are "husebyOverSSH" , "ServerOverSSH", "AgentOverSSH"
	 API tags "Dependency"
	

### How to get report ### 
* Once execution complete go to target folder and open extent report CrosscodeReport.html 

### Process Automation to CI/CD 
* **Automation**
  * create a feature branch from "integration"
  * develop on the feature branch
  * test locally on VM
  * update the pipeline script to reduce time of run
  * test on jenkins running the feature branch
  * jenkins once clears the feature branch, then get the latest from integration to feature
  * test on jenkins running the feature branch
  * build passes, then revert the changes to pipeline script (Note: changes to pipeline script are part of CI/CD)
  * test once, on jenkins
  * raise a PR

from here on process will move to next step:

* **CI/CD**
  * review the PR for code changes to ensure that there is no possible impact on the CI.
  * any need for refactoring will be communicated back to be clubbed together with the coding of the next feature branch
  * merge & squash PR and feature branch restored
  * run build on Jenkins
  * build fails, then run again to be sure nothing environmental
  * build again fails, then PR changes will be reverted on integration and communicated back to Automation member to fix the issue
  * build passes, then delete the feature branch
  * raise PR for integration->qa
  * merge & squash PR and integration branch restored
  * run build on Jenkins
  * build fails, then try again
  * build fails again, then investigate the reason
  * build passes, all good
