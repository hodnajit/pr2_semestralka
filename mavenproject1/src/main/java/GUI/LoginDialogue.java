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
 *
 * @author Jituška zub
 */
public class LoginDialogue extends JFrame {

    private JPasswordField password;
    private JTextField username;
    private JButton login;
    private JButton exit;
    private WindowManager windowManager;
    private String pass;
    private String user;

    public LoginDialogue(WindowManager w) {
        this.windowManager = w;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");
        initComponents();
        pack();
    }

    public void initComponents() {
        
        password = new JPasswordField("password");
        username = new JTextField("username");
        login = new JButton("login");
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
                    windowManager.logginToJira(new JiraClient(user, pass));
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
