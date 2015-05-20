package Tests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import comm.JiraClient;
import java.net.URISyntaxException;
import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jituška zub
 */
public class JiraClientTest {

    JiraClient jC;
    JiraClientTest jCT;
    String password = "iddqdiddqd";
    String username = "FilipK";

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
}
