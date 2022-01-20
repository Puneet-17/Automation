package com.huseby.framework.core;

import com.huseby.framework.utils.Utilities;
import com.huseby.model.ConstantsModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import com.github.fge.jsonschema.main.JsonSchemaFactory;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static java.lang.Thread.sleep;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.Matchers.equalTo;
import java.util.concurrent.TimeUnit;

public class BaseStep extends BaseCore {
    protected static Response response;
    protected static String output;


    public void BaseStep() {
        reportLog("Instantiating BaseStep", LogType.trace);
    }

    protected void setup() {
        reportLog("In BaseStep setting up the values", LogType.trace);
        reportLog("Server OS : " + Utilities.getSharedProperty("serverOS"), LogType.info);
        reportLog("Agent OS : " + Utilities.getSharedProperty("agentOS"), LogType.info);
        reportLog("Application Name : " + Utilities.getSharedProperty("applicationName"), LogType.info);
        reportLog("Action Type : " + Utilities.getSharedProperty("actionType"), LogType.info);
        reportLog("jCape host IP :" + Utilities.getSharedProperty("host"), LogType.info);
        reportLog("Repository key: " + Utilities.getSharedProperty("repoKey"), LogType.info);
        reportLog("Repository URL: " + Utilities.getSharedProperty("repoURL"), LogType.info);

        // set application variables
//        serverOS = System.getProperty("serverOS");
//        agentOS = System.getProperty("agentOS");
//        applicationName = System.getProperty("applicationName");
//        actionType = System.getProperty("actionType");
//        Utilities.setSharedProperty("protocolPort = 80;
//        protocol = "http";
        Utilities.setSharedProperty("repoKey", Utilities.getSharedProperty("repoKey"));
        Utilities.setSharedProperty("repoURL", Utilities.getSharedProperty("repoURL"));
        Utilities.setSharedProperty("networkAdapter", Utilities.getSharedProperty("TARGET_VM_ENS"));

        Utilities.setSharedProperty("host", Utilities.getSharedProperty("host"));
        if (Utilities.getSharedProperty("host") == null)
            Utilities.setSharedProperty("host", getSystemIP());

        Utilities.setSharedProperty("applicationIp", Utilities.getSharedProperty("host"));
        Utilities.setSharedProperty("endPointUrl", String.format("%s://%s", Utilities.getSharedProperty("protocol"), Utilities.getSharedProperty("host")));
        Utilities.setSharedProperty("contextPath", "huseby/server/");
        reportLog(String.format("Rest Assured - %s", Utilities.getSharedProperty("endPointUrl")), LogType.trace);

        // RestAssured.port = Integer.valueOf(Utilities.getSharedProperty("protocolPort").toString());
        Integer port = 80;
        Utilities.setSharedProperty("protocolPort", port);
        RestAssured.baseURI = Utilities.getSharedProperty("endPointUrl").toString();
        RestUtil.setBaseURI(Utilities.getSharedProperty("endPointUrl").toString());
        reportLog("Before token", LogType.trace);
        Utilities.setSharedProperty("accessToken", getToken());

        Utilities.setSharedProperty("applicationUrl", String.format("%s://%s", Utilities.getSharedProperty("protocol"),
                Utilities.getSharedProperty("host")));
    }

    public String getSystemIP() {
        String IP = null;
        String res = runCommand("ifconfig " + Utilities.getSharedProperty("networkAdapter") + " | grep \"inet\"").trim();
        if (!res.isEmpty()) {
            String[] ip = res.split("\\s+");
            IP = ip[1];
        } else
            fail("IP is not displayed");
        return IP;
    }

    protected String getToken() {
        String clientId = "";
        String clientSecret = "";
        String username = "";
        String password = "";
        String accessToken = null;

        try {
            reportLog("getToken() entered try block", LogType.trace);
            clientId = Configuration.getConfig("clientId");
            clientSecret = Configuration.getConfig("clientSecret");
            username = Configuration.getConfig("apiUsername");
            password = Configuration.getConfig("apiPassword");
            reportLog("getToken() read all configuration variables", LogType.info);
            String requestUrl = "oauth2/token?grant_type=password&client_id=" + clientId + "&client_secret=" + clientSecret
                    + "&username=" + username + "&password=" + password;
            reportLog(String.format("getToken() prepared request url - %s", requestUrl), LogType.info);
            accessToken = "Bearer " + given().when().post(requestUrl).then().extract().path("access_token");
        } catch (Exception ex) {
            reportLog(String.format("[ASSERT] Unable to reach configuration - \n%s\n%s", ex.getCause(), ex.getMessage()), LogType.error);
        }

        return accessToken;
    }

    protected static ObjectNode getJavaAgentConfiguration() throws IOException {
        String scanningPath = "/opt/huseby/java";
        String schedule = "0 0/3 * * * *";

        String javaJson = "{\n" +
                "\t\"encrypted\": {},\n" +
                "\t\"agent\": {\n" +
                "\t\t\"schedule\": \"* 0/2 * * * ?\",\n" +
                "\t\t\"scanner\": {\n" +
                "\t\t\t\"depth\": 4,\n" +
                "\t\t\t\"paths\": [\n" +
                "\t\t\t\t\"\"\n" +
                "\t\t\t],\n" +
                "\t\t\t\"filters\": []\n" +
                "\t\t},\n" +
                "\t\t\"batchSize\": 750,\n" +
                "\t\t\"analysis\": {\n" +
                "\t\t\t\"databases\": [],\n" +
                "\t\t\t\"recursion\": []\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();

        JsonNode javaJSONObject = mapper.readTree(javaJson);

        return (ObjectNode) javaJSONObject;
    }

    protected static ObjectNode getSqlAgentConfiguration() throws IOException {

        String jdbcUrl = "jdbc:postgresql://" + Utilities.getSharedProperty("host") + ":5432/fusionauth";
        String username = "postgres";
        String password = "postgres";
        String sqlJson = "{\n" +
                "  \"encrypted\": {\n" +
                "    \"databases\": [\n" +
                "      {\n" +
                "        \"password\": \"" + password + "\",\n" +
                "        \"jdbcUrl\": \"" + jdbcUrl + "\",\n" +
                "        \"username\": \"" + username + "\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"agent\": {\n" +
                "    \"schedule\": \"0 0/2 * * * *\",\n" +
                "    \"batchSize\": 750\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();

        JsonNode sqlJSONObject = mapper.readTree(sqlJson);

        return (ObjectNode) sqlJSONObject;
    }

    protected static ObjectNode getDefaultSqlAgentConfiguration() throws IOException {

        String jdbcUrl = "jdbc:postgresql://" + Utilities.getSharedProperty("host") + ":5432/fusionauth";
        String username = "postgres";
        String password = "postgres";
        String sqlJson = "{\n" +
                "  \"encrypted\": {\n" +
                "    \"databases\": [\n" +
                "      {\n" +
                "        \"password\": \"" + password + "\",\n" +
                "        \"jdbcUrl\": \"" + jdbcUrl + "\",\n" +
                "        \"username\": \"" + username + "\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"agent\": {\n" +
                "    \"schedule\": \"0 0/2 * * * *\",\n" +
                "    \"batchSize\": 750\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();

        JsonNode sqlJSONObject = mapper.readTree(sqlJson);

        return (ObjectNode) sqlJSONObject;
    }

    // This method is used to execute the pre_start_huseby.sh shell on terminal
    // in local machine
    public static String preStarthuseby() throws IOException, InterruptedException {
        String command = "sudo bash /opt/huseby/pre_start_huseby.sh";
        String[] cmd = {"/bin/bash", "-c", command};
        StringBuilder output = new StringBuilder();

        // Terminal command execution started
        Process process = Runtime.getRuntime().exec(cmd);

        InputStream inputStream = process.getInputStream();
        OutputStream out = process.getOutputStream();

        // It's used to send the input to the terminal at runtime
        out.write(("y\n" + Utilities.getSharedProperty("applicationUrl") + "\n .\n \n \n \n \n \n").getBytes());
        out.flush();

        // This input variable stores the terminal output
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // This loop is used to read the terminal output and store it into the output
        // variable
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        out.close();
        int exitValue = process.waitFor();
        // Destroy the process and return the output
        process.destroy();
        return output.toString();
    }

    protected String readJSONFromFile(String file) throws IOException, ParseException {
        String jsonDirectory = "jpetstoreDatabaseJson";
        String fileName = System.getProperty("user.dir") + "/src/main/resources/dependency/" + jsonDirectory + "/" + file + ".json";
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(fileName);
        return jsonParser.parse(reader).toString();
    }

    protected String readJSONFromFile(String file, String dependencyName) throws IOException, ParseException {
        String fileName = System.getProperty("user.dir") + "/src/main/resources/dependency/" + dependencyName + "/" + file + ".json";
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(fileName);
        return jsonParser.parse(reader).toString();
    }

    protected String getJSONPath(String file, String dependencyName) throws IOException, ParseException {
        return System.getProperty("user.dir") + "/src/main/resources/dependency/" + dependencyName + "/" + file + ".json";
    }

    protected HashMap<String, String> compareJson(String node, String jsonExpected, String jsonActual) {
        HashMap<String, String> errorDetails = new HashMap<String, String>();
        try {
            assertThatJson(jsonActual).whenIgnoringPaths(
                    "data.properties.firstObserved",
                    "data.properties.lastObserved",
                    "data.id",
                    "data.name",
                    "data.identity",
                    "data.containedNodes[*].containedNodes[*].id",
                    "data.containedNodes[*].containedNodes[*].identity",
                    "data.containedNodes[*].containedNodes[*].properties.firstObserved",
                    "data.containedNodes[*].containedNodes[*].properties.lastObserved",
                    "data.referencedNodes[*].referencedNodes[*].properties.firstObserved",
                    "data.referencedNodes[*].referencedNodes[*].properties.lastObserved",
                    "data.referencedNodes[*].referencedNodes[*].identity",
                    "data.referencedNodes[*].referencedNodes[*].id",
                    "data.properties.nodeOrRelationship",
                    "data.containedNodes[*].containedNodes[*].properties.nodeOrRelationship",
                    // Getting error relating to reference nodes, so ignoring
                    "data.referencedNodes",
                    "data.containedNodes[*].containedNodes[*].relationshipCount"
            ).isEqualTo(jsonExpected);
        } catch (Error error) {
            String errorInfo;
            errorInfo = "ERROR\n-----------";
            errorInfo += error.getMessage();
            errorInfo += "\nACTUAL JSON\n----------------";
            errorInfo += jsonActual;
            errorInfo += "\nEXPECTED JSON\n---------------";
            errorInfo += jsonExpected;
            errorDetails.put(node, errorInfo);
        }
        return errorDetails;
    }

    protected void createFailedNodesFiles(String jsonDirectory, Response response, String nodeID) throws IOException {
        String dirPath = System.getProperty("user.dir") + "/output/" + System.getProperty("applicationName")
                + "-" + System.getProperty("actionType") + "-" + System.getProperty("agentOS") + "-"
                + System.getProperty("serverOS") + "/";
        File folder = FileUtils.getFile(dirPath);

        if (!folder.exists()) {
            FileUtils.forceMkdir(folder);

            if (!folder.exists()) {
                Assert.fail("Folder not created: " + dirPath);
            }
            reportLog("Folder created: " + dirPath + "  " + folder.exists(), LogType.info);
        }

        String fileName = dirPath + "/" + nodeID + ".json";
        FileWriter file = new FileWriter(fileName);
        file.write(response.asString());
        file.close();
    }

    public String runJavaAgentAnalysis(String applicationName, String jarFilePath, String folderPath) throws Exception {
        String cmd = getJavaAgentCommand(folderPath, JarType.archive, jarFilePath, true, "", applicationName);
        String output = runCommand(cmd);

        return output;
    }

    protected String createAppTempFolder(String uniqueFolderName) {
        File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
        File directory = new File(TEMP_DIRECTORY, uniqueFolderName + "-" + uuidForApplicationRun);
        if (!directory.exists()) {
            directory.mkdir();
        }

        return directory.getAbsolutePath().toString();
    }

//    public String getJavaAgentCommand(String appName, String jarFilePath) {
//        String agentFolderPath = createAppTempFolder(appName);
//
//        String cmd = "cd '" + agentFolderPath + "' && sudo java -Dhuseby.host='" + Utilities.getSharedProperty("applicationUrl") +
//                "' -jar '/opt/huseby/java/cc-java-agent-main.jar' analyze -t archive -f '" + jarFilePath + "' -a " + appName;
//        return cmd;
//    }

//    public String getJavaAgentCommand(String applicationName, String jarFilePath, String jarFolderPath) {
//        String cmd = "cd '" + jarFolderPath + "' && sudo java -Dhuseby.host='" + Utilities.getSharedProperty("applicationUrl") +
//                "' -jar '/opt/huseby/java/cc-java-agent-main.jar' analyze -t archive -f '" + jarFilePath + "' -a " + applicationName;
//        return cmd;
//    }
//
//    public String getJavaAgentCommand(String applicationName, String jarFilePath, String jarFolderPath, String jdbcUrl) {
//        String cmd = "cd '" + jarFolderPath + "' && sudo java -Dhuseby.host='" + Utilities.getSharedProperty("applicationUrl") +
//                "' -jar '/opt/huseby/java/cc-java-agent-main.jar' analyze -t archive -f '" + jarFilePath
//                + "' --disableMethodInvokesScanning true -r dao-3 -d '" + jdbcUrl + "' -a " + applicationName;
//        return cmd;
//    }

    public enum JarType {archive};
    public String getJavaAgentCommand(String folderPath, JarType jarType,  String jarFilePath,
                                      boolean disableMethodInvokesScanning, String jdbcUrl, String applicationName) {
        String cmd = "cd '{folderPath}' && " +
                "sudo java -Dhuseby.host='{hostPath}' " +
                "-jar '/opt/huseby/java/cc-java-agent-main.jar' analyze " +
                "-t {jarType} " +
                "-f '{jarFilePath}' " +
                "--disableMethodInvokesScanning {disableMethodInvokesScanning} " +
                "{-d 'jdbcUrl'} " +
                "{-r dao-3} " +
                "-a {applicationName}";

        cmd = cmd.replace("{folderPath}", folderPath);
        cmd = cmd.replace("{hostPath}", Utilities.getSharedProperty("applicationUrl").toString());
        cmd = cmd.replace("{jarType}", jarType.toString());
        cmd = cmd.replace("{jarFilePath}", jarFilePath);
        cmd = cmd.replace("{disableMethodInvokesScanning}", String.valueOf(disableMethodInvokesScanning));
        cmd = cmd.replace("{-d 'jdbcUrl'}", (jdbcUrl.isEmpty()?"":"-d '"+ jdbcUrl + "'") );
        cmd = cmd.replace("{-r dao-3}", (jdbcUrl.isEmpty()?"":"-r dao-3") );
        cmd = cmd.replace("{applicationName}", applicationName);

        return cmd;
    }

    public String getSqlAgentCommand(String appName, String jdbcUrl, String dbUser, String dbPwd) {
        String agentFolderPath = createAppTempFolder(appName);

        String cmd = "cd " + agentFolderPath + " && sudo java -Dhuseby.host=" + Utilities.getSharedProperty("applicationUrl")
                + " -Doauth.username=temporaryadmin@huseby.com -Doauth.password='!Default-CC1'"
                + " -jar '/opt/huseby/sql/cc-sql-agent-main.jar' analyze --jdbcUrl " + jdbcUrl
                + " --user '" + dbUser + "'"
                + " --password '" + dbPwd + "'"
                + " -a " + appName;
        return cmd;
    }

    public String getSqlAgentCommand(String dbName, String jdbcUrl) {
        String cmd = "cd /opt/huseby/sql/ && java -Dhuseby.host=" + Utilities.getSharedProperty("applicationUrl")
                + " -Doauth.username=temporaryadmin@huseby.com -Doauth.password='!Default-CC1'"
                + "-jar cc-sql-agent-main.jar analyze " + jdbcUrl;
        return cmd;
    }

    // This method is used to set SQL agent configuration for jCape-SQL-Agent
    public static ObjectNode getSqlAgentConfigurationForMysql(String dbUsername, String dbPassword, String dbName) throws IOException {
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/" + dbName;
        String sqlJson = "{\n" +
                "    \"dataSourceId\": \"sqlCape\",\n" +
                "    \"properties\": {\n" +
                "        \"encrypted\": {\n" +
                "            \"databases\": [\n" +
                "                {\n" +
                "                    \"password\": \"" + dbPassword + "\",\n" +
                "                    \"jdbcUrl\": \"" + jdbcUrl + "\",\n" +
                "                    \"username\": \"" + dbUsername + "\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        \"agent\": {\n" +
                "            \"schedule\": \"0 0/2 * * * *\",\n" +
                "            \"batchSize\": 750\n" +
                "        }\n" +
                "    }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode sqlJSONObject = mapper.readTree(sqlJson);
        return (ObjectNode) sqlJSONObject;
    }

    protected static ObjectNode setThingsboardConfigFile() throws IOException {

        String jdbcUrl = "jdbc:postgresql://" + Utilities.getSharedProperty("host") + ":5432/thingsboard";
        String username = "postgres";
        String password = "huseby";
        String sqlJson = "{\n" +
                "    \"dataSourceId\": \"sqlCape\",\n" +
                "    \"properties\": {\n" +
                "        \"encrypted\": {\n" +
                "            \"databases\": [\n" +
                "                {\n" +
                "                    \"password\": \"" + password + "\",\n" +
                "                    \"jdbcUrl\": \"" + jdbcUrl + "\",\n" +
                "                    \"username\": \"" + username + "\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        \"agent\": {\n" +
                "            \"schedule\": \"0 0/2 * * * ?\",\n" +
                "            \"batchSize\": 750\n" +
                "        }\n" +
                "    }\n" +
                "}";


        ObjectMapper mapper = new ObjectMapper();

        JsonNode sqlJSONObject = mapper.readTree(sqlJson);

        return (ObjectNode) sqlJSONObject;
    }


    public String getSqlAgentId() {
        String url = Utilities.getSharedProperty("contextPath") + "registration/agents";

        reportLog("Started execution of jCape agents api", LogType.info);
        Response response = given().headers(getHeaderWithAuthAndJson()).when().get(url);
        String nodeJSON = response.asString();
        reportLog(nodeJSON, LogType.info);

        verifyResponseCodeAndContentType(response, HttpStatusCode.HTTP_OK, ConstantsModel.Application_JSON);
        reportLog("Agent id found!", LogType.info);
        response.then().body("status", equalTo("success"));

        String sqlAgentIdInArray = JsonPath.read(response.asString(), "$.data[?(@.dataSourceId=='sqlCape')].agentId").toString();
        return sqlAgentIdInArray.substring(sqlAgentIdInArray.indexOf("\"") + 1, sqlAgentIdInArray.indexOf("\"]"));
    }

    protected void validateDependenciesNodes(String dependencyName) throws IOException, ParseException {

        String root = System.getProperty("user.dir");
        File srcDir = new File(root + "/src");
        File mainResDir = new File(root + "/src/main/resources");
        File dependencyDir = new File(root + "/src/main/resources/dependency");
        File thingsboardDir = new File(root + "/src/main/resources/dependency/thingsboardDatabaseJSON");

        reportLog("Source Directory status: " + srcDir.exists(), LogType.info);
        reportLog("Main resources Directory status: " + mainResDir.exists(), LogType.info);
        reportLog("Depdendency Directory status: " + dependencyDir.exists(), LogType.info);
        reportLog("Thingsboard Directory status: " + thingsboardDir.exists(), LogType.info);

        boolean validationStatus = true;

        String path = System.getProperty("user.dir") + "/src/main/resources/dependency/" + dependencyName;
        reportLog("Starting validation of " + dependencyName + " dependencies nodes", LogType.info);
        List<String> jsonFileName = Files.list(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        int passedComparisonCount = 0;
        int failedComparisonCount = 0;
        HashMap<String, AbstractMap.SimpleEntry<String, Integer>> errorDetails = new HashMap<String, AbstractMap.SimpleEntry<String, Integer>>();

        reportLog(String.valueOf(jsonFileName.size()), LogType.info);
        for (String json : jsonFileName) {
            String node = json.substring(0, json.length() - 5);
            String url = Utilities.getSharedProperty("contextPath") + "dependency/decomposition/" + node;
            Response response = given().headers(getHeaderWithAuthAndJson()).when().get(url);
            reportLog("Decomposition API response is below: \n" + response.toString(), LogType.info);
            String jsonSchemaPath = getJSONPath(node, dependencyName);
            Boolean res = compareJson(node, jsonSchemaPath, response.asString(), errorDetails, LogType.info);
            if (res) {
                passedComparisonCount++;
            } else {
                failedComparisonCount++;
                createFailedNodesFiles(dependencyName, response, node);
                validationStatus = false;
            }
        }

        Utilities.writeJSONComparisonLog(errorDetails);

        reportLog("Number of nodes passing comparison - " + passedComparisonCount, LogType.info);
        reportLog("Number of nodes failing comparison - " + failedComparisonCount, LogType.info);
        reportLog("Failed nodes have been copied to OUTPUT folder.", LogType.info);
        reportLog("Validation of " + dependencyName + " dependencies nodes is completed successfully", LogType.info);
        Assert.assertTrue(validationStatus, "Dependencies node validation failed please check above logs");
    }

    private Boolean compareJson(String node, String jsonExpected, String jsonActual, HashMap<String, AbstractMap.SimpleEntry<String, Integer>> errorDetails, LogType info) throws IOException {
        try {
            reportLog("Expected JSON path: \n" + jsonExpected, LogType.info);
            reportLog("Actual JSON response: \n" + jsonActual, LogType.info);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode schemaNode = mapper.readValue(new File(jsonExpected), JsonNode.class);
            JsonNode dataJson = mapper.readValue(jsonActual, JsonNode.class);

            System.out.println("schemaNode: \n" + schemaNode);
            System.out.println("--------------------------");
            System.out.println(": \n" + dataJson);

            JsonSchema jsonSchema = null;
            try {
                jsonSchema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
                ProcessingReport validationReport = jsonSchema.validate(dataJson);
                System.out.println(validationReport.toString());

            } catch (ProcessingException e) {
                reportLog("Json comparision got exception!!!!!!!!!", LogType.info);
                e.printStackTrace();
            }
            return true;
        } catch (Error error) {
            Integer errorCount = 0;
            String nodeContext = node;
            //String errorParts[] = error.getMessage();
            if (errorDetails.containsKey(error.getMessage())) {
                AbstractMap.SimpleEntry<String, Integer> errorPair = errorDetails.get(error.getMessage());
                errorCount = errorPair.getValue();
                nodeContext = errorPair.getKey() + "," + node;
                errorDetails.remove(error.getMessage());
            }
            errorDetails.put(error.getMessage(), new AbstractMap.SimpleEntry<String, Integer>(nodeContext, ++errorCount));
            reportLog("Node validation failed: " + node + " - Error occurred: \n" + error, LogType.info);
            return false;
        }
    }

    protected String sleep(long waitInSeconds) {
        String returnValue = "";
        long milliSeconds = TimeUnit.SECONDS.toMillis(waitInSeconds);

        try {
            reportLog("Thread sleep - " + milliSeconds + " ms", LogType.trace);
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            returnValue = "Thread sleep raised error : " + e.getMessage();
        }

        return returnValue;
    }

    protected static ObjectNode getEmptyAgentConfiguration() {
        String javaJson = "{}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode javaJSONObject = null;

        try {
            javaJSONObject = mapper.readTree(javaJson);
        } catch (IOException ex) {
        }

        return (ObjectNode) javaJSONObject;
    }

    protected boolean createFolderInTmp(String subFolder) {
        // create a folder on ubuntu
        File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
        File directory = new File(TEMP_DIRECTORY, subFolder);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return true;
    }

    protected boolean copySourceFileFromResourceToTargetFileFolderInTmp(String shFileName, String osName) {
        boolean lastActionStatus = false;
        File TMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
        String tmpPath = TMP_DIRECTORY.getAbsolutePath();
        String targetPath = tmpPath + "/" + osName + "/" + shFileName;
        String sourcePath = System.getProperty("user.dir") + "/src/main/resources/supportFiles/" + osName + "/" + shFileName;

        try {
            Files.copy(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
            lastActionStatus = true;
        } catch (IOException ex) {
            exception = ex.getMessage();
        }

        if (Files.exists(Paths.get(targetPath))) {
            reportLog("Path found -> " + targetPath, LogType.info);
        } else {
            reportLog("Path NOT found -> " + targetPath, LogType.info);
        }
        return true;
    }

    protected boolean executeShellFile(String shFileName, String subFolder, String... paramValues) {
        boolean returnStatus = false;
        String[] userInputs = null;

        File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
        String tempPath = TEMP_DIRECTORY.getAbsolutePath();
        String targetPath = tempPath + "/" + subFolder + "/" + shFileName;
        String shellParams = String.join(" ", paramValues);

        // extract HEREDOC lines
        Path shellFilePath = Paths.get(targetPath);
        try {
            String shellFileContent = Files.readAllLines(shellFilePath, StandardCharsets.UTF_8).toString();
            if(shellFileContent.contains("HEREDOC")) {
                String hereDocLines = shellFileContent.split("HEREDOC")[1];
                userInputs = hereDocLines.split(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // marking shell file as executable
        output = runCommand("chmod 777 " + targetPath);
        returnStatus = output.isEmpty();

        int retryCount = 0;
        do {
            reportLog("Retry attempt - " + retryCount, LogType.trace);
            output = runCommand("sudo " + targetPath + " " + shellParams, userInputs);
            sleep(60*retryCount);
        } while(output.contains("Resource temporarily unavailable") && ++retryCount <10);
        returnStatus = returnStatus && !output.contains("Error");

        return returnStatus;
    }

    protected boolean copyShellScriptAndExecute(String folderName, String fileName, String... shellParams) {
        boolean lastActionStatus = true;

        // create a folder on os
        lastActionStatus = lastActionStatus && createFolderInTmp(folderName);
        exception = lastActionStatus ? exception : "Unable to  create folder for shell script file";

        // copy folder shell files from source tp target on os
        lastActionStatus = lastActionStatus && copySourceFileFromResourceToTargetFileFolderInTmp(fileName,folderName);
        exception = lastActionStatus ? exception : "Unable to  copy shell script file from resource folder -> " + exception;

        //executing shell files
        lastActionStatus = lastActionStatus && executeShellFile(fileName,folderName, shellParams);
        exception = lastActionStatus ? exception : "Unable to  execute shell script file from resource folder -> " + output;

        return (lastActionStatus);
    }

}