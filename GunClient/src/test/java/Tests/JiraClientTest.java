package Tests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.BasicProject;
import comm.JiraClient;
import java.net.URISyntaxException;
import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Class for testing JiraClient for connecting to jira
 * @author Jituška zub
 */
public class JiraClientTest {

    JiraClient jC;
    JiraClientTest jCT;
    String password = "iddqdiddqd";
    String username = "FilipK";
/**
 * Constructor
 * @throws bad uri adress
 * @throws Something happened
 * @throws Query is bad
 */
    public JiraClientTest() throws URISyntaxException, ClassNotFoundException, JSONException {
        jC = new JiraClient(username, password);
    }

    @BeforeClass
    public static void setUpClass() throws URISyntaxException {
    }

    @AfterClass
    public static void tearDownClass() {
    }
    // @Test
    // public void hello() {}
    /**
    * Test examples, when queue is empty
    */
    @Test
    public void testEmptyQueue() {
        assertNull(jC.getIssueQueue());
        jC.doQueueOfDoneIssues();
        assertNotNull(jC.getIssueQueue());
        jC.clearQueue();
        assertNull(jC.getIssueQueue());
        jC.updateIssueQueue();
        assertNull(jC.getIssueQueue());
    }
    /**
     * Test number of cartridges
     */
    @Test
    public void testCartridges(){
        jC.updateIssueQueue();
        int cartridgesJC = jC.getCartridges();
        assertEquals(cartridgesJC, jC.getIssueQueue().size());
        jC.shoot();
        assertEquals(cartridgesJC-1,jC.getCartridges());
        jC.updateIssueQueue();
        assertTrue(cartridgesJC-1<=jC.getCartridges());
    }
    /**
     * Test connecting to server with good and bad authentification.
     * @throws URISyntaxException
     * @throws ClassNotFoundException
     * @throws JSONException 
     */
    @Test
    public void testClientConnected() throws URISyntaxException, ClassNotFoundException, JSONException{
        password = "wrongPassword";
        jC = new JiraClient(username, password);
        assertFalse(jC.didAnythingReturn());
        password = "iddqdiddqd";
        jC = new JiraClient(username, password);
        assertTrue(jC.didAnythingReturn());
    }
    /**
     * Try to connect with bad authentification, expected exception
     * @throws URISyntaxException
     * @throws ClassNotFoundException
     * @throws JSONException 
     */
    @Test(expected=RestClientException.class)
    public void testException() throws URISyntaxException, ClassNotFoundException, JSONException{
        password = "wrongPassword";
        jC = new JiraClient(username, password);
        JiraRestClient rC = jC.getRestClient();
        BasicProject bc = rC.getProjectClient().getProject("TPR").claim();
    }
}
