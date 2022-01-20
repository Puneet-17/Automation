package com.huseby.steps.base;

import com.huseby.framework.core.BaseStep;
import com.huseby.framework.utils.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static io.restassured.RestAssured.given;

public class BaseVerifyRulesMatchesSteps extends BaseStep {
    protected static String ticketNumber;
    protected static String ruleIds="";



    protected boolean analyzeJar(String jarFileName) {
        boolean returnValue = false;
        File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
        String tempPath = TEMP_DIRECTORY.getAbsolutePath();
        String folderPath =  tempPath + "/" + ticketNumber+"-"+uuidForApplicationRun;
        String jarFilePath = folderPath + "/" + ticketNumber+ "-" + uuidForApplicationRun + ".jar";
        String applicationName = ticketNumber+ "-" +uuidForApplicationRun;

        // prepare analysis command
        String analysisCommand = getJavaAgentCommand(folderPath, JarType.archive, jarFilePath, true, "", applicationName);
        try {
            output = runCommand(analysisCommand);
            assertEquals(output.contains("Successfully posted entity to neo4cape api."), true,"java analysis either failed or did not complete timely");
            returnValue = true;
        } catch (Exception ex){
            exception = ex.getMessage();
        }

        return returnValue;
    }

    protected boolean createFolderBasedOnTicketNumber() {
        File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
        File directory = new File(TEMP_DIRECTORY,ticketNumber +"-"+uuidForApplicationRun);
        if (!directory.exists()){
            directory.mkdir();
        }

        return true;
    }

    protected String getTargetFilePath(String targetFileName){
        File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
        String tempPath = TEMP_DIRECTORY.getAbsolutePath();
        String shFilePath = tempPath + "/" + ticketNumber + "-"+uuidForApplicationRun+"/" + targetFileName;
        return shFilePath;
    }

    protected boolean copySourceFileFromResourceToTargetFileFolder(String resourceFileName, String targetFileName, String rulesSubFolder) {
        boolean returnValue = false;
        String targetPath = getTargetFilePath(targetFileName);
        String sourcePath = System.getProperty("user.dir") + "/src/main/resources/rulesMatches/"+ rulesSubFolder + "/" + resourceFileName;

        try {
            Files.copy(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
            returnValue = true;
        } catch (IOException ex) {
            exception = ex.getMessage();
        }

        if(Files.exists(Paths.get(targetPath))){
            reportLog("Path found -> " + targetPath, LogType.info);
        } else {
            reportLog("Path NOT found -> " + targetPath, LogType.info);
        }

        return returnValue;
    }

}
