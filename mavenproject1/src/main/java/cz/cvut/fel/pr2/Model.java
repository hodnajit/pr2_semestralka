/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.pr2;

import com.atlassian.jira.rest.client.ComponentRestClient;
import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.MetadataRestClient;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.SearchRestClient;
import com.atlassian.jira.rest.client.SessionRestClient;
import com.atlassian.jira.rest.client.UserRestClient;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 *
 * @author Jituška zub
 */
public class Model {

    private String yourUsername;
    private String yourPassword;

    JerseyJiraRestClientFactory factory;
    URI jiraServerUri;
    JiraRestClient restClient;
    ArrayList<Issue> issueQueu;

    public Model(String userName, String password) throws URISyntaxException {
        this.yourUsername = userName;
        this.yourPassword = password;

        this.jiraServerUri = new URI("https://applifting.atlassian.net");
        this.factory = new JerseyJiraRestClientFactory();;
        this.restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, yourUsername, yourPassword);
    }

    private ArrayList<Issue> getAllIssues() {
        return issueQueu;
    }

    public int countDoneIssues(Issue issue) {
        int doneIssue = 0;

        return doneIssue;
    }

    public String getYourUsername() {
        return yourUsername;
    }

    public void setYourUsername(String yourUsername) {
        this.yourUsername = yourUsername;
    }

    public String getYourPassword() {
        return yourPassword;
    }

    public void setYourPassword(String yourPassword) {
        this.yourPassword = yourPassword;
    }

    public static void main(String[] args) {
        //System.out.println("Hello World!");
    }
}
