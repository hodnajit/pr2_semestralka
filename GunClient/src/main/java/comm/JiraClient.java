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
import com.atlassian.jira.rest.client.domain.Authentication;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.ServerInfo;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;

/**
 * JiraClient to communicate with atlassian server. Mostly is send http request
 * to get http respons by JSON query.
 *
 * @author JituĹˇka zub
 */
public class JiraClient {

    /**
     * username of user
     */
    private String jiraUsername;
    /**
     * password of user
     */
    private String jiraPassword;
    /**
     * url of atlassian server, it is inicialized on
     * http://applifting.atlassian.net, that is applifting server
     */
    private String url = "http://applifting.atlassian.net";
    //date of first updated issue, cant be earlier. Toto bude defaultnĂ­, pokud nebude uloĹľenĂˇ jinĂˇ hodnota, jinak se tato hodnota bude naÄŤĂ­tat z nÄ›jakĂ©ho souboru toho uĹľivatele.
    /**
     * DateTime is date of last updated issue (the last one, that was loaded in
     * JSON query). It is inicialized on date of first issue in applifting
     * server.
     */
    private static DateTime dateOfLastUpdatedDoneIssue = new DateTime(2015, 1, 1, 1, 1, 1, 1, DateTimeZone.UTC);

    /**
     * Key of last updated issue. (Chech dateOfLastUpdatedDoneIssue for more
     * information)
     */
    private static String keyOfLastUpdatedIssue;
    /**
     * status of issue, that we want to get from server for done task (issues)
     * it s status = Máme to!
     */
    private String statusOfIssues = "'Máme to!'";  //Máme to

    /**
     * Factory to create jira Client
     */
    private final JiraRestClientFactory factory;
    /**
     * URI of atlassian server
     */
    private final URI jiraServerUri;
    /**
     * rest client
     */
    private final JiraRestClient restClient;
    private final NullProgressMonitor pm;
    /**
     * queue of done issues, here we save the JSON query
     */
    private List<String> issueQueue;

    /**
     * Number of cartridges for shooting, it is inicialized on zero
     */
    private int cartridges = 0;
    
    Authentication authentication;

    /**
     * Constructor for create JiraClient, it sets username and password of user,
     * create jiraClient, create issueQueue and save here the JSON query, that
     * we get from http request. Set cartridges (queuery.size) and clear
     * issueQueue (it's no longer needed).
     *
     * @param userName
     * @param password
     * @throws When problem with URL of server is
     * @throws When there is no file to load
     * @throws When there is a problem with JSON query
     */
    public JiraClient(String userName, String password) throws URISyntaxException, ClassNotFoundException, JSONException {
        this.jiraUsername = userName;
        this.jiraPassword = password;

        factory = new AsynchronousJiraRestClientFactory();
        jiraServerUri = new URI(url);
        restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, jiraUsername, jiraPassword);
        pm = new NullProgressMonitor();
    }

    /**
     * This method is called in doQueueOfDoneIssues() and it remember key and
     * date of last updated issue from entered project
     *
     * @param projectKey Project key of actual project that it is going throw
     * @param is first issue in that project (last updated in that project)
     */
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

    /**
     * This method create issueQueue from done issues(their keys) ordered by
     * update date
     */
    public void doQueueOfDoneIssues() {
        System.out.println("Fuck that!");
        issueQueue = new ArrayList<String>();
        //System.out.println("Jdu vypsat tasky:");
        for (BasicProject project : restClient.getProjectClient().getAllProjects().claim()) {
            System.out.println("and that");
            String key = project.getKey();
            //System.out.println("Project: " + key);
            Promise<SearchResult> searchJqlPromise = restClient.getSearchClient().searchJql("(project = '" + key + "' AND status = " + statusOfIssues + " AND assignee = " + jiraUsername + ") ORDER BY updated");
            for (BasicIssue BIssue : searchJqlPromise.claim().getIssues()) {
                System.out.println("everything!");
                String isKey = BIssue.getKey();
                //System.out.println("Issue: " + isKey);
                Promise<Issue> issue = restClient.getIssueClient().getIssue(isKey);
                getDateOfLastUpdatedIssue(key, issue.claim());  //safe the last updated issues
                issueQueue.add(BIssue.getKey());
            }
        }

    }

    /**
     * Methods decreace cartridge, after is has been shot
     */
    public void shoot() {
        if (cartridges <= 0) {
            //DO NOTHING
        } else {
            cartridges--;
        }
    }

    private void loadprofile() throws ClassNotFoundException {
        try {
            FileInputStream fileIn = new FileInputStream(jiraUsername + ".issueQueue.serialized");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            dateOfLastUpdatedDoneIssue = (DateTime) in.readObject();
            String keyOfLastUpdatedIssue = (String) in.readObject();
            int cartridges = in.readInt();
            in.close();
            fileIn.close();
            System.out.printf("Done");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void saveProfile() {
        try {
            FileOutputStream fileOut = new FileOutputStream("profile" + jiraUsername);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(dateOfLastUpdatedDoneIssue);
            out.writeObject(keyOfLastUpdatedIssue);
            out.write(cartridges);
            out.close();
            fileOut.close();
            Logger.getLogger(JiraClient.class.getName()).log(Level.FINE, "Saving profile was done.");

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * This method call doQueueofDoneIssue - makes new queue of new issues. Add
     * new cartridges if there is
     */
    public void updateIssueQueue() {
        doQueueOfDoneIssues();
        cartridges = sumProjectile();
        issueQueue.clear();
    }

    /**
     * Methods count projectiles to gun
     *
     * @return number of new projectiles (cartridges)
     */
    private int sumProjectile() {
        return this.issueQueue.size();
    }
    
    public boolean didAnythingReturn(){
        try{
            //BasicProject bc = restClient.getProjectClient().getProject("TPR").claim();
            System.out.println("true");
            return true;
        }catch(RestClientException rCE){
            System.out.println("Serou na mě.");
            return false;
        }
    }
    

    /**
     * Getter to get number of cartridges
     *
     * @return number of actual cartridges
     */
    public int getCartridges() {
        return cartridges;
    }
    /**
     * Getter to get queue of tasks, almost NULL
     *
     * @return queue with done tasks
     */
    public List<String> getIssueQueue() {
        return issueQueue;
    }
    /**
     * Clear queue;
     */
    public void clearQueue(){
        issueQueue.clear();
    }
}
