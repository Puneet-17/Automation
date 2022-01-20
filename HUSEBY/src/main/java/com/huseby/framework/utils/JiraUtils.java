package com.huseby.framework.utils;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.huseby.framework.core.BaseCore;
import com.huseby.framework.core.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.sun.jersey.core.util.Base64;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class JiraUtils extends BaseCore {
	private String jiraUser;
	private String accessKey;
	private String jiraBaseUrl;
	private String auth;

	public JiraUtils() throws Exception {
		this.jiraUser = Configuration.getConfig("JiraUser");
		this.accessKey = Configuration.getConfig("JiraAccessKey");
		this.jiraBaseUrl = "https://crosscode.atlassian.net/rest/api/3";
		auth = new String(Base64.encode(this.jiraUser + ":" + accessKey));
		RestAssured.baseURI = this.jiraBaseUrl;
	}

	/**
	 * Get the issue information
	 *
	 * @param issueId
	 * @return issue information in JSON format
	 */
	public String getIssueInfo(String issueId) {
		String url = "/issue/" + issueId + "?fields=*all&fieldsByKeys=false";
		Response response = given()
				.contentType(ContentType.JSON)
				.auth()
				.preemptive()
				.basic(this.jiraUser, this.accessKey)
				.get(url)
				.then().extract().response();

		return response.getBody().asString();
	}

	/**
	 * Get the linked issues
	 *
	 * @param issueId
	 * @return list of the linked issue numbers
	 */
	public List<String> getLinkedIssues(String issueId) {
		String issueInfoJSON = getIssueInfo(issueId);
		List<String> linkedOutwardIssues = JsonPath.read(issueInfoJSON, "$.fields.issuelinks[*].outwardIssue.key");
		List<String> linkedInwardIssues = JsonPath.read(issueInfoJSON, "$.fields.issuelinks[*].inwardIssue.key");
		linkedOutwardIssues.addAll(linkedInwardIssues);
		return linkedOutwardIssues;
	}

	public List<JiraIssue> getLinkedOpenIssues(String issueId) throws ParseException {
		List<String> linkedIssueIds = getLinkedIssues(issueId);
		List<JiraIssue> linkedIssueWithOpenResolution = new ArrayList<JiraIssue>();
		for (String linkedIssueId : linkedIssueIds) {
			String issueInfoJSON = getIssueInfo(linkedIssueId);
			reportLog("Linked issue info (" + linkedIssueId + ") " + issueInfoJSON, LogType.info);
			Object resolution = JsonPath.read(issueInfoJSON, "$.fields.resolution");
			if (resolution == null) {
				JiraIssue jiraIssue = new JiraIssue(issueInfoJSON);
				linkedIssueWithOpenResolution.add(jiraIssue);
			}
		}

		return linkedIssueWithOpenResolution;
	}

	public void linkIssues(String issue, String issuesNeedsToBeLinked, String linkType) {

	}

	public String createJiraIssue(String projectId, String issueSummary, String issueType, String userAssignTo,
								  String reporterUser, String description) {
		String jsonData = "{ \"fields\": { \"project\": { \"id\": \"" + projectId + "\" }, "
				+ "\"summary\":\"" + issueSummary + "\", \"issuetype\":{ \"name\": \"" + issueType + "\"}, \"assignee\": "
				+ "{ \"name\": \"" + userAssignTo + "\"}, "
				+ " \"description\": \"" + description + "\"}}";

		Response response = given().header("Authorization", "Basic " + auth).header("Content-Type", "application/json")
				.body(jsonData).when().post("/rest/api/2/issue");
		String BugId = response.then().extract().path("key");
		return BugId;
	}

	public void uploadFileInDefect(String issueId, String file) {
		Response response = given().header("Authorization", "Basic " + auth).header("X-Atlassian-Token", "no-check")
				.multiPart("file", new File(file)).
				when().post("/rest/api/2/issue/" + issueId + "/attachments");
		System.out.println(response.asString());
	}

	/**
	 * Add comment in issue
	 *
	 * @param issueId : provide issue id
	 * @param comment : provide you comment
	 */
	public Response addComment(String issueId, String comment) {
		String json = "{ \"body\": \"" + comment + "\"}";
		System.out.println(json);
		Response response = given().header("Authorization", "Basic " + auth).header("Content-Type", "application/json")
				.body(json).when().post("/rest/api/2/issue/" + issueId + "/comment");
		System.out.println(response.asString());
		return response;
	}

	/**
	 * Get transitions details of task
	 *
	 * @param taskId : task Id
	 * @return
	 */
	public Response getResolutionOfTask(String taskId) {
		Response response = given().header("Authorization", "Basic " + auth).header("Content-Type", "application/json")
				.when().get("/rest/api/2/issue/" + taskId + "/transitions");
		return response;
	}

	public String checkIssue(String summaryText) {
		Response response = given().header("Authorization", "Basic " + auth).header("Content-Type", "application/json")
				.when().get("/rest/api/2/search?jql=project=10429 and issuetype=Bug");
		int total = response.then().extract().path("total");
		for (int i = 0; i < total; i++) {
			String bugType = response.then().extract().path("issues[" + i + "].fields.issuetype.name");
			String summary = response.then().extract().path("issues[" + i + "].fields.summary");
			String resolution = response.then().extract().path("issues[" + i + "].fields.status.name");

			String key = response.then().extract().path("issues[" + i + "].key");
			if (summary != null && bugType.equals("Bug") && !resolution.equals("Closed")
					&& summary.contains(summaryText)) {
				return key;
			}
		}
		return "Not Found";
	}

	public static void main(String args[]) throws Exception {
		JiraUtils jiraTicket = new JiraUtils();
		List<JiraIssue> linkedIssues = jiraTicket.getLinkedOpenIssues("QA-2135");
		for (JiraIssue jiraIssue : linkedIssues) {
			jiraIssue.getAllDetails();
		}
	}
}
