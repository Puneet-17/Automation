package com.huseby.framework.core;

import ch.qos.logback.classic.Level;
import com.huseby.framework.utils.JiraIssue;
import com.huseby.framework.utils.JiraUtils;
import com.huseby.framework.utils.Utilities;
import io.cucumber.java.Scenario;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONTokener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.UUID;


public class BaseCore extends Assert {
    private static final Logger logger = LoggerFactory.getLogger(BaseCore.class);
    protected String exception;
    private static WebDriver webDriver;
    private static Scenario scenario;
    protected static final String uuidForApplicationRun = UUID.randomUUID().toString();

    protected static enum LogType {
        debug,
        trace,
        info,
        error
    }


    public void BaseCore() {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.valueOf(Utilities.getSharedProperty("logLevel").toString()));
        reportLog("Instantiating BaseCore",LogType.trace);
    }

    protected void setScenario(Scenario currentScenario) {
        scenario = currentScenario;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    protected WebDriver getWebDriver() {
        return webDriver;
    }

    protected void setWebDriver(WebDriver webDriver) {
        BaseCore.webDriver = webDriver;
    }

    protected void reportLog(String logMessage, LogType logType ) {
        switch (logType){
            case info:
                logger.debug(logMessage);
                break;
            case debug:
                logger.debug(logMessage);
                break;
            case trace:
                logger.trace(logMessage);
                break;
            case error:
                logger.error(logMessage);
                break;
        }
    }

    protected boolean setupWebDriver(){
        boolean returnValue = false;
        returnValue = Utilities.getSharedProperty("agentBrowser").toString().isEmpty();
        if(!returnValue)
        {
            switch(Utilities.getSharedProperty("agentBrowser").toString()){
                case "FireFox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--headless");
                    io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver().setup();
                    webDriver = new FirefoxDriver(firefoxOptions);
                    break;
                case "Chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--headless");
                    io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
                    webDriver = new ChromeDriver(chromeOptions);
                    break;
                case "Edge":
                    io.github.bonigarcia.wdm.WebDriverManager.edgedriver().setup();
                    webDriver = new EdgeDriver();
                    break;
            }
        }

        returnValue = returnValue ? returnValue : (webDriver!=null);

        return returnValue;
    }

    public String runCommand(String command, String... userInputValues)  {
        StringBuilder output = new StringBuilder();
        String[] cmd;

        reportLog("This command is being executed: " + command, LogType.info);

        cmd = new String[]{"/bin/bash", "-c", command};

        // Terminal command execution started
        ProcessBuilder builder = new ProcessBuilder(cmd);

        File tmpFile = null;
        FileWriter writer = null;
        try {
            if (userInputValues!=null) {
                tmpFile = File.createTempFile("envInput", ".tmp");
                writer = new FileWriter(tmpFile);
                for (String userInputValue : userInputValues) {
                    if (!userInputValue.trim().isEmpty()) {
                        writer.write(userInputValue + "\n");
                    }
                }
                writer.close();
                builder.redirectInput(tmpFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        builder.redirectErrorStream(true);

        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            return e.getMessage();
        }
        // This input variable stores the terminal output
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // This loop is used to read the terminal output and store it into the output
        // variable
        Thread runThread = new Thread(new Runnable() {
            public void run() {
                String line;
                try {
                    while ((line = input.readLine()) != null) {
                        reportLog(line, LogType.trace);
                        output.append(line);
                    }
                } catch (IOException ex) {
                    reportLog("Suppress IOException to ensure results of run not lost, exception generate is - " + ex.getMessage(),LogType.error);
                }
            }
        }
        );
        runThread.start();
        synchronized (runThread) {
            try {
                runThread.wait(72000000);
            } catch (InterruptedException e) {
                return e.getMessage();
            }
        }
        if(process.isAlive()) {
            process.destroy();
        }
        return output.toString();
    }

    public String runCommandWithTerminationString(String command, boolean print,String terminationString)  {
        StringBuilder output = new StringBuilder();
        String[] cmd;

        reportLog("This command is being executed: " + command, LogType.info);

        cmd = new String[]{"/bin/bash", "-c", command};

        // Terminal command execution started
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            return e.getMessage();
        }

        // This input variable stores the terminal output
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // This loop is used to read the terminal output and store it into the output
        // variable
        Thread runThread = new Thread(new Runnable() {
            public void run() {
                String line;
                try {
                    while ((line = input.readLine()) != null) {

                        if (print) {
                            System.out.println(line);
                        }
                        output.append(line);

                        if(line.contains(terminationString))
                            break;
                    }
                } catch (IOException ex) {
                    reportLog("Suppress IOException to ensure results of run not lost, exception generate is - " + ex.getMessage(),LogType.error);
                }
            }
        }
        );
        runThread.start();
        synchronized (runThread) {
            try {
                runThread.wait(72000000);
            } catch (InterruptedException e) {
                return e.getMessage();
            }
        }
        if(process.isAlive()) {
            process.destroy();
        }
        return output.toString();
    }

    public String runCommandWithWait(String command, int waitInMilli, boolean print) {
        StringBuilder output = new StringBuilder();
        String[] cmd;

        reportLog("This command is being executed: " + command, LogType.info);

        cmd = new String[]{"/bin/bash", "-c", command};

        // Terminal command execution started
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            return "Error - " + e.getMessage();
        }

        // This input variable stores the terminal output
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // This loop is used to read the terminal output and store it into the output
        // variable
        Thread runThread = new Thread(new Runnable() {
            public void run() {
                String line;
                try {
                    while ((line = input.readLine()) != null) {

                        if (print) {
                            System.out.println(line);
                        }
                        output.append(line);
                    }
                } catch (IOException ex) {
                    reportLog("Suppress IOException to ensure results of run not lost, exception generate is - " + ex.getMessage(),LogType.error);
                }
            }
        }
        );
        runThread.start();
        synchronized (runThread) {
            try {
                runThread.wait(waitInMilli);
            } catch (InterruptedException e) {
                return "Error - " + e.getMessage();
            }
        }
        if(process.isAlive()) {
            process.destroy();
        }
        return output.toString();
    }

    // This method is used to execute command with user input in local machine
    public static String runCommandWithInput(String command, String input, boolean print)
            throws IOException, InterruptedException {

        StringBuilder output = new StringBuilder();

        String[] cmd;
        cmd = new String[]{"/bin/bash", "-c", command};

        // Terminal command execution started
        Process process = Runtime.getRuntime().exec(cmd);

        OutputStream out = process.getOutputStream();
        InputStream inputStream = process.getInputStream();

        // It's used to send the input to the terminal at runtime
        out.write((input).getBytes());
        out.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        // This loop is used to read the terminal output and store it into the output
        // variable
        while ((line = reader.readLine()) != null) {
            if (print) {
                System.out.println(line);
            }
            output.append(line);
        }
        int exitValue = process.waitFor();

        // Destroy the process and return the output
        process.destroy();
        return output.toString();
    }

    public static String runCommandWithInputOnAgent(String command, String input, boolean print) {
        final StringBuilder sBuilder = new StringBuilder();
        logger.trace("Spawning runner thread");
        Thread runThread = new Thread(new Runnable() {
            public void run() {
                logger.trace("Inside runner run()");
                try {
                    logger.trace("Inside runner calling runCommand");
                    String response = "";
                    int retryAttempts = 0;
                    do {
                        response = runCommandWithInput(command, input, print);
                        if(response.contains("Waiting for cache lock:")) {
                            Thread.sleep(30000);
                        } else {
                            break;
                        }
                        ++retryAttempts;
                    } while(retryAttempts < 5);

                    sBuilder.append(response);
                } catch (Exception ex) {
                    logger.trace("Suppress exception to ensure results of run not lost, exception generate is - " + ex.getMessage());
                }
            }
        }
        );
        logger.trace("Going to start runner thread");
        runThread.start();
        logger.trace("Wait on runner thread to complete");
        synchronized (runThread) {
            try {
                runThread.wait();
            } catch (InterruptedException e) {
                return "Error - " + e.getMessage();
            }
        }
        logger.trace("Exited runner thread");
        return (sBuilder.toString());
    }

    public void runCommandWithWaitAndLog(String command, int waitInMilli)
            throws IOException, InterruptedException {

        String[] cmd;
        cmd = new String[]{"/bin/bash", "-c", command};

        reportLog("runCommandWithWaitAndLog -> " + command,LogType.info);

        // Terminal command execution started
        Process process = Runtime.getRuntime().exec(cmd);
        Thread.sleep(waitInMilli);
        process.destroyForcibly();
    }

    public Headers getHeaderWithAuthAndJson() {
        String authto = Utilities.getSharedProperty("accessToken").toString();
        Header header1 = new Header("Authorization", authto);
        Header header2 = new Header("accept", "*/*");
        Header header3 = new Header("Content-Type", "application/json");
        List<Header> list = new LinkedList<Header>();
        list.add(header1);
        list.add(header2);
        list.add(header3);
        return new Headers(list);
    }

    public void jsonSchemaValidator(Response response, String schemaFileName) {
        InputStream inputStream = getClass().getResourceAsStream("/jsonschema/" + schemaFileName + ".json");
        reportLog("Obtained the jsonSchema file", LogType.trace);
        org.json.JSONObject rawSchema = new org.json.JSONObject(new JSONTokener(inputStream));
        org.json.JSONObject jsonResponse = new org.json.JSONObject(response.asString());
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(jsonResponse);
        reportLog("API json response with jsonSchema is validated", LogType.trace);
    }

    protected void verifyResponseCodeAndContentType(Response response, int code, String contentType) {
        RestUtil.verifyResponseCode(response, code);
        RestUtil.verifyResponseContentType(response, contentType);
    }

    protected void saveMapToFile(HashMap<String, String> nodeJSONMap, String fileWithPath ) throws IOException {
        File fileMasterJSON=new File(fileWithPath);
        FileOutputStream fos=new FileOutputStream(fileMasterJSON);
        ObjectOutputStream oos=new ObjectOutputStream(fos);

        oos.writeObject(nodeJSONMap);
        oos.flush();
        oos.close();
        fos.flush();
        fos.close();
    }

    protected String createPostgresDatabase(String dbName) throws Exception {
        String output = runCommand("sudo -u postgres -h " + Utilities.getSharedProperty("currentNodeIP")
                + " createdb " + dbName);
        return output;
    }

    protected String restorePostgresDatabase(String dbName, String backupFileWithPath) throws Exception {
        String output = runCommand("sudo -u postgres psql -d" + dbName + " < " + backupFileWithPath);
        return output;
    }

    protected String getPostgresJDBCUrl(String dbName) {
        String output = "jdbc:postgresql://localhost:5432/"
                 + dbName;
        return output;
    }

    protected String sleep(long waitInSeconds) {
        String returnValue="";

        try {
            Thread.sleep(waitInSeconds*1000);
        } catch (InterruptedException e) {
            returnValue = "Thread sleep raised error : " +  e.getMessage();
        }

        return returnValue;
    }
    Collection<String> scenarioTickets = new ArrayList<String>();

    protected void extractTicketsFromScenarioTags(Scenario scenario) {
        for(String tag :  scenario.getSourceTagNames()) {
            if(tag.startsWith("@TEST"))
                scenarioTickets.add(tag.replace("@TEST_","").replace("@TESTSET_",""));
        }
    }

    protected String getMessageForOpenTickets()  {
        String messageForOpentickets = "";
        JiraUtils jiraUtils = null;
        try {
            jiraUtils = new JiraUtils();
        } catch (Exception e) {
            return "Error - " + e.getMessage();
        }
        for(String ticket: scenarioTickets) {
            // call jira method to get all open tickets linked to ticket
            List<JiraIssue> openLinkedIssues = null;
            try {
                openLinkedIssues = jiraUtils.getLinkedOpenIssues(ticket);
            } catch (ParseException e) {
                return "Error - " + e.getMessage();
            }
            // generate the message from the list of open tickets
            for(JiraIssue openIssue: openLinkedIssues) {
                if(openIssue.getIssueType().equals("Bug") || openIssue.getIssueType().equals("Inquiry")
                        || openIssue.getIssueType().equals("Task")
                        || openIssue.getIssueType().equals("Story")) {
                    messageForOpentickets += messageForOpentickets.isEmpty() ? "" : ", ";
                    messageForOpentickets += openIssue.getKey();
                    messageForOpentickets += "[" + openIssue.getAssignee() + "]";
                    messageForOpentickets += " : " + openIssue.getSummary();
                    messageForOpentickets += "\n";
                }
            }
        }
        messageForOpentickets = messageForOpentickets.isEmpty()?"" :" Scenario skipped since it has open tickets: \n" + messageForOpentickets;

        return messageForOpentickets;
    }
}
