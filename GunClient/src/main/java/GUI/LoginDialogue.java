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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.codehaus.jettison.json.JSONException;

/**
 * This Class is window (JFrame) for logging to atlassian server. You add
 * username and password and then log in by button login. You can also switch
 * application by pressing button exit. It extends JFrame.
 *
 * @author Jituška zub
 */
public class LoginDialogue extends JFrame implements KeyListener{

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
    private boolean passwordClicked = false;
    private boolean usernameClicked = false;
    public LoginDialogue(WindowManager w) {
        this.windowManager = w;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GunClient - Login");
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
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        
        password.addKeyListener(this);
        password.addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent e) {
                if (!passwordClicked){
                    password.setText("");
                    passwordClicked = true;
                }else{
                    //DO NOTHING...
                }
                
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (password.getPassword().length==0){
                    password.setText("password");
                    passwordClicked = false;
                }
            }
            
        });
        username.addKeyListener(this);
        username.addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent e) {
                if (!usernameClicked){
                    username.setText("");
                    usernameClicked = true;
                }else{
                    //DO NOTHING...
                }
                
            }

            @Override
            public void focusLost(FocusEvent e) {
                if ("".equals(username.getText())){
                    username.setText("username");
                    usernameClicked = false;
                }
            }

            
        });
        exit.addKeyListener(this);
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });
        login.addKeyListener(this);
        login.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                char[] tmp = password.getPassword();
                int len = tmp.length;
                for (int i = 0; i < len; i++) {
                    pass += tmp[i];
                }
                user = username.getText();
                /*pass = "iddqdiddqd"; //I should not push this to repo, but I did...
                user = "FilipK";*/
                try {
                    JiraClient jC = new JiraClient(user, pass);
                    if(!jC.didAnythingReturn()){
                        JOptionPane.showMessageDialog(null, "Wrong username or password, try again.", "Wrong authentification.", JOptionPane.WARNING_MESSAGE);
                    }else{
                        windowManager.loginToJira(jC);
                    }                    
                } catch (URISyntaxException ex) {
                    Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "URISyntax error with connecting to jira.", ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "Error in loading documents.", ex);
                } catch (JSONException ex) {
                    Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "Bad identification.", ex);
                }
            }
        });
        getContentPane().add(username);
        getContentPane().add(password);
        getContentPane().add(login);
        getContentPane().add(exit);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }else if(key == KeyEvent.VK_ENTER){
            char[] tmp = password.getPassword();
                int len = tmp.length;
                for (int i = 0; i < len; i++) {
                    pass += tmp[i];
                }
                user = username.getText();
                try {
                    windowManager.loginToJira(new JiraClient(user, pass));
                } catch (URISyntaxException ex) {
                    Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "URISyntax error with connecting to jira.", ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "Error in loading documents.", ex);
                } catch (JSONException ex) {
                    Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "Bad identification.", ex);
                }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //do nothing
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //do nothing
    }

}
