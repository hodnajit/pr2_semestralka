/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import Service.WindowManager;
import comm.Client;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Class for testing Client to connect to device.
 * @author Jituška zub
 */
public class ClientTest {
    Client c;
    /**
     * Attention, that will stop working, when device will be elsewhere! It is test for running on test IP Adress at home
     * Host - ip of device
     */     
    String host = "10.0.0.3";
    /**
     Attention, that will stop working, when device will be elsewhere! It is test for running on test port at home
     * Port - port of device
     */
    int port = 1710;
    private WindowManager wM;
    
    public ClientTest() {        
        c = new Client(host, port, wM);
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
