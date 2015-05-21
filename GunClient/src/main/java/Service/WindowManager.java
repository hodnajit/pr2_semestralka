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

    /**
     * Instance of connectDialogue
     */
    private ConnectDialogue cd;
    /**
     * Instance of LoginDialogue
     */
    private LoginDialogue ld;
    /**
     * Instance of main Windo
     */
    private MainWindow mw;
    /**
     * instance of jira client
     */
    private JiraClient jc;
    /**
     * instance of client
     */
    private Client c;

    /**
     * Constructor of WindowManager. It create all windows (LoginDialogue,
     * connectDialogue, MainWindow) First loginWindow, then connectWindow and
     * then main window are visible
     */
    public WindowManager() {
        ld = new LoginDialogue(this);
        ld.setVisible(true);
    }

    /**
     * loginDialogue dispose and show connect window
     *
     * @param jirC instance of jiraClient
     */
    public void loginToJira(JiraClient jirC) {
        jc = jirC;
        ld.dispose();
        this.showConnectWindow();

    }

    /**
     * show ConnectDialogue, if main window still running, dispose it
     */
    public void showConnectWindow() {
        if (mw != null) {
            mw.dispose();
        }
        cd = new ConnectDialogue(this);
        cd.setVisible(true);
    }

    /**
     * Try to connect to server on rpi
     *
     * @param host ip adress of rpi
     * @param port port of rpi
     */
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
