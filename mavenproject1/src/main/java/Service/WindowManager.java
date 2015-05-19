/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import GUI.ConnectDialogue;
import GUI.LoginDialogue;
import GUI.MainWindow;
import comm.Client;
import comm.JiraClient;

/**
 *
 * @author Jituška zub
 */
public class WindowManager {
    ConnectDialogue cd;
    LoginDialogue ld;
    MainWindow mw;
    JiraClient jc;
    Client c;
    
    
    public WindowManager(){
        cd = new ConnectDialogue(this);
        cd.setVisible(true);
        ld = new LoginDialogue(this);
        ld.setVisible(false);
        mw = new MainWindow(this, jc);
        mw.setVisible(false);
    }

    public void connectedToClient(Client client) {
        c = client;
        cd.dispose();
        ld.setVisible(true);
    }
    
    public void logginToJira(JiraClient jirC){
        jc = jirC;
        ld.dispose();
        mw.setVisible(true);
        
    }
}
