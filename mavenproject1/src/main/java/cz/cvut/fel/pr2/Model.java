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
import com.atlassian.jira.rest.client.*;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author JituĹˇka zub
 */
public class Model {

    private String jiraUsername;
    private String jiraPassword;
    private String url = "http://applifting.atlassian.net";
    //date of first updated issue, cant be earlier. Toto bude defaultní, pokud nebude uložená jiná hodnota, jinak se tato hodnota bude načítat z nějakého souboru toho uživatele.
    private static DateTime dateOfLastUpdatedDoneIssue = new DateTime(2013, 8, 14, 7, 39, 31, 412, DateTimeZone.UTC);
    private static String keyOfLastUpdatedIssue;
    private String statusOfIssues = "'Máme to!'";

    private final JiraRestClientFactory factory;
    private final URI jiraServerUri;
    private final JiraRestClient restClient;
    private final NullProgressMonitor pm;
    private List<String> issueQueue;

    public Model(String userName, String password) throws URISyntaxException, ClassNotFoundException {
        this.jiraUsername = userName;
        this.jiraPassword = password;

        factory = new AsynchronousJiraRestClientFactory();
        jiraServerUri = new URI(url);
        restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, jiraUsername, jiraPassword);
        pm = new NullProgressMonitor();
        loadQueue();
        issueQueue = new ArrayList<String>();
        doQueueOfDoneIssues();
    }

    //This method go throw all project of User and find the last updated issue from them. To safe time I take queue of done issues ordered by name and then by updated date. I can check the first one of each project.
    private void getDateOfLastUpdatedIssue(String projectKey, Issue is) {
        String lastProjectKey = null;
        DateTime dateOfIs;
        int compare;
        while (lastProjectKey != projectKey) {
            lastProjectKey = projectKey;

            dateOfIs = is.getUpdateDate();
            compare = dateOfIs.compareTo(dateOfLastUpdatedDoneIssue);
            if (compare > 0) {
                dateOfLastUpdatedDoneIssue = dateOfIs;
                keyOfLastUpdatedIssue = is.getKey();
            } else {
                //do nothing
            }
        }
    }

    private void doQueueOfDoneIssues() {
        for (BasicProject project : restClient.getProjectClient().getAllProjects().claim()) {
            String key = project.getKey();
            Promise<SearchResult> searchJqlPromise = restClient.getSearchClient().searchJql("(project = '" + key + "' AND status = " + statusOfIssues + " AND assignee = " + jiraUsername + ") ORDER BY updated");
            for (BasicIssue BIssue : searchJqlPromise.claim().getIssues()) {
                String IsKey = BIssue.getKey();
                Promise<Issue> issue = restClient.getIssueClient().getIssue(IsKey);
                getDateOfLastUpdatedIssue(key, issue.claim());  //safe the last updated issues
                issueQueue.add(BIssue.getKey());
            }
        }

    }

    protected void deleteIssue() {
        boolean isDelete = false;
        int i = 0;
        while (!isDelete) {
            if (issueQueue.get(i) != keyOfLastUpdatedIssue || issueQueue.size() == 1) {
                issueQueue.remove(i);
            } else {
                //do nothing
            }
            i++;
        }
    }

    private void loadQueue() throws ClassNotFoundException {
        ArrayList<String> issueQueueRefresh = null;
        try {
            FileInputStream fileIn = new FileInputStream(jiraUsername + ".issueQueue.serialized");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            issueQueueRefresh = (ArrayList<String>) in.readObject();
            in.close();
            fileIn.close();
            System.out.printf("Done");
        } catch (IOException i) {
            i.printStackTrace();
        }
        issueQueue = issueQueueRefresh;
    }

    private void safeQueue() {
        try {
            FileOutputStream fileOut = new FileOutputStream(jiraUsername + ".issueQueue.serialized");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(issueQueue);
            out.close();
            fileOut.close();
            System.out.println("Done");

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    protected void updateIssueQueue() {
        doQueueOfDoneIssues();
    }

    protected int sumProjectile() {
        return this.issueQueue.size();
    }
}
