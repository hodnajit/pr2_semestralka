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
    }

    public void loginToJira(JiraClient jirC) {
        jc = jirC;
        ld.dispose();
        this.showConnectWindow();

    }

    public void showConnectWindow() {
        if(mw != null)mw.dispose();
        cd = new ConnectDialogue(this);
        cd.setVisible(true);
    }

    public void connectToClient(String host, int port) {
        this.c = new Client(host, port, this);
        Thread t = new Thread(c);
        t.setDaemon(true);
        t.start();
        cd.dispose();
        mw = new MainWindow(this, jc, c);
        mw.setVisible(true);
    }
}
