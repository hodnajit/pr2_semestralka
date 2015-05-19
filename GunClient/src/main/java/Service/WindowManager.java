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
 * This class manage all windows in application. It remember all windows and
 * clients in running application
 *
 * @author Jituška zub
 */
public class WindowManager {

    ConnectDialogue cd;
    LoginDialogue ld;
    MainWindow mw;
    JiraClient jc;
    Client c;

    /**
     * Constructor of WindowManager. It create all windows (LoginDialogue,
     * connectDialogue, MainWindow) First loginWindow, then connectWindow and
     * then main window are visible
     */
    public WindowManager() {
        ld = new LoginDialogue(this);
        ld.setVisible(true);
        cd = new ConnectDialogue(this);
        cd.setVisible(false);
        mw = new MainWindow(this, jc, c);
        mw.setVisible(false);
    }

    public void connectedToClient(Client client) {
        c = client;
        cd.dispose();
        mw.setVisible(true);

    }

    public void logginToJira(JiraClient jirC) {
        jc = jirC;
        ld.dispose();
        cd.setVisible(true);

    }
}
