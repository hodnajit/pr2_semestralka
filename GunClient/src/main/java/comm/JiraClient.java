/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.*;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import org.codehaus.jettison.json.JSONException;

/**
 *
 * @author JituĹˇka zub
 */
public class JiraClient {

    private String jiraUsername;
    private String jiraPassword;
    private String url = "http://applifting.atlassian.net";
    //date of first updated issue, cant be earlier. Toto bude defaultnĂ­, pokud nebude uloĹľenĂˇ jinĂˇ hodnota, jinak se tato hodnota bude naÄŤĂ­tat z nÄ›jakĂ©ho souboru toho uĹľivatele.
    private static DateTime dateOfLastUpdatedDoneIssue = new DateTime(2013, 8, 14, 7, 39, 31, 412, DateTimeZone.UTC);
    private static String keyOfLastUpdatedIssue;
    private String statusOfIssues = "'MĂˇme to!'";  //Máme to

    private final JiraRestClientFactory factory;
    private final URI jiraServerUri;
    private final JiraRestClient restClient;
    private final NullProgressMonitor pm;
    private List<String> issueQueue;
    private boolean logOK = false;
    private int cartridges = 0;

    public JiraClient(String userName, String password) throws URISyntaxException, ClassNotFoundException, JSONException {
        this.jiraUsername = userName;
        this.jiraPassword = password;

        factory = new AsynchronousJiraRestClientFactory();
        jiraServerUri = new URI(url);
        restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, jiraUsername, jiraPassword);
        pm = new NullProgressMonitor();
        issueQueue = new ArrayList<String>();
        //loadQueue();
        doQueueOfDoneIssues();
        cartridges = sumProjectile();
    }

    //This method finds lastUpdatedIssue (last one done) in project. Issues in project are sorted by updated date.
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
        System.out.println("Jdu vypsat tasky:");
        for (BasicProject project : restClient.getProjectClient().getAllProjects().claim()) {
            String key = project.getKey();
            System.out.println("Project: " + key);
            Promise<SearchResult> searchJqlPromise = restClient.getSearchClient().searchJql("(project = '" + key + "' AND status = " + statusOfIssues + " AND assignee = " + jiraUsername + ") ORDER BY updated");
            for (BasicIssue BIssue : searchJqlPromise.claim().getIssues()) {
                String isKey = BIssue.getKey();
                System.out.println("Issue: " + isKey);
                Promise<Issue> issue = restClient.getIssueClient().getIssue(isKey);
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
        cartridges--;
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

    public void safeQueue() {
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

    public void updateIssueQueue() {
        doQueueOfDoneIssues();
    }

    private int sumProjectile() {
        return this.issueQueue.size();
    }
}
