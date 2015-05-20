/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Service.WindowManager;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import comm.JiraClient;
import org.codehaus.jettison.json.JSONException;

/**
 * This Class is window (JFrame) for logging to atlassian server. You add
 * username and password and then log in by button login. You can also switch
 * application by pressing button exit. It extends JFrame.
 *
 * @author Jituška zub
 */
public class LoginDialogue extends JFrame {

    /**
     * PasswordField to add password of user
     */
    private JPasswordField password;
    /**
     * TextField to add username of user
     */
    private JTextField username;
    /**
     * Buttons login and exit
     */
    private JButton login, exit;
    /**
     * Window manager to manage all windows in app
     */
    private WindowManager windowManager;
    /**
     * Strings pass (password) and user (username) to create JiraClient
     */
    private String pass, user;

    /**
     * Constructor of LoginDialogue window, needs windowManager to manage
     * window, it call method initComponents() to init components in frame
     *
     * @param w windowManager that manages windows in app
     */
    public LoginDialogue(WindowManager w) {
        this.windowManager = w;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");
        initComponents();
        pack();
    }

    /**
     * That methods inits components in frame and add actionListeners to buttons
     */
    private void initComponents() {

        password = new JPasswordField("password");
        username = new JTextField("username");
        login = new JButton("log in");
        exit = new JButton("exit");
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });
        login.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                char[] tmp = password.getPassword();
                int len = tmp.length;
                for (int i = 0; i < len; i++) {
                    pass += tmp[i];
                }
                user = username.getText();
                try {
                    pass = "iddqdiddqd";
                    user = "FilipK";
                    JiraClient jc = new JiraClient(user, pass);
                    windowManager.logginToJira(jc);                    
                } catch (URISyntaxException ex) {
                    Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "URISyntax error with connecting to jira.", ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "Error in loading documents.", ex);
                } catch (JSONException ex) {
                    Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "Bad identification.", ex);
                }
            }

        });
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        getContentPane().add(username);
        getContentPane().add(password);
        getContentPane().add(login);
        getContentPane().add(exit);
    }
}
