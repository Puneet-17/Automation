package com.huseby.framework.utils;

import com.jayway.jsonpath.JsonPath;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JiraIssue {
    String key;
    String issueType;
    String summary;
    List<String> issueDescription;
    String status;
    String assignee;
    Date dateReported;

    JiraIssue(String jsonData) throws ParseException {
        String key = JsonPath.read(jsonData, "$.key");
        String issueType = JsonPath.read(jsonData, "$.fields.issuetype.name");
        String summary = JsonPath.read(jsonData, "$.fields.summary");
        Object description = JsonPath.read(jsonData, "$.fields.description");
        List<String> issueDescription = (description != null) ? JsonPath.read(jsonData, "$.fields.description.content[*].content[*].text") : new ArrayList<String>();
        String status = JsonPath.read(jsonData, "$.fields.status.name");
        Object assigneeObject = JsonPath.read(jsonData, "$.fields.assignee");
        String assignee = (assigneeObject != null) ? JsonPath.read(jsonData, "$.fields.assignee.displayName") : "";
        String date = JsonPath.read(jsonData, "$.fields.created"); //Format: "2021-07-12T18:57:49.226-0700"
        Date createdDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ").parse(date);

        this.key = key;
        this.issueType = issueType;
        this.summary = summary;
        this.issueDescription = issueDescription;
        this.status = status;
        this.assignee = assignee;
        this.dateReported = createdDate;
    }

    public String getKey() {
        return key;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getIssueDescription() {
        return issueDescription;
    }

    public String getStatus() {
        return status;
    }

    public String getAssignee() {
        return assignee;
    }

    public Date getDateReported() {
        return dateReported;
    }

    public void getAllDetails() {
        System.out.println("Key: " + getKey());
        System.out.println("Issue type: " + getIssueType());
        System.out.println("Summary: " + getSummary());
        System.out.println("Issue description: " + getIssueDescription());
        System.out.println("Status: " + getStatus());
        System.out.println("Assignee: " + getAssignee());
        System.out.println("Date reported: " + getDateReported());
        System.out.println("---------------------------------------");
    }
}
