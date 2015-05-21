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
public class LoginDialogue extends JFrame implements KeyListener {

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
     * JiraClient
     */
    private JiraClient jC;
    /**
     * Booleans to see if user clicked to textfields password and username
     */
    private boolean passwordClicked = false;
    private boolean usernameClicked = false;

    /**
     * Constructor of LoginDialogue window, needs windowManager to manage
     * window, it call method initComponents() to init components in frame
     *
     * @param w windowManager that manages windows in app
     */
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
        password.addFocusListener(new FocusListener() {

            /**
             * Modify FocusListener - after clicked on TextField,
             * passwordClicked = true
             *
             * @param e event of TextField password
             */
            @Override
            public void focusGained(FocusEvent e) {
                if (!passwordClicked) {
                    password.setText("");
                    passwordClicked = true;
                } else {
                    //DO NOTHING...
                }

            }

            /**
             * After focus Lost - if it is still empty, set password text on
             * "password" and passwordClicked = false;
             *
             * @param e event of textfield
             */
            @Override
            public void focusLost(FocusEvent e) {
                if (password.getPassword().length == 0) {
                    password.setText("password");
                    passwordClicked = false;
                }
            }

        });
        username.addKeyListener(this);
        username.addFocusListener(new FocusListener() {
            /**
             * Modify FocusListener - after clicked on TextField,
             * usernameClicked = true
             *
             * @param e event of TextField username
             */
            @Override
            public void focusGained(FocusEvent e) {
                if (!usernameClicked) {
                    username.setText("");
                    usernameClicked = true;
                } else {
                    //DO NOTHING...
                }

            }

            /**
             * After focus Lost - if it is still empty, set username text on
             * "username" and usernameClicked = false;
             *
             * @param e event of textfield
             */
            @Override
            public void focusLost(FocusEvent e) {
                if ("".equals(username.getText())) {
                    username.setText("username");
                    usernameClicked = false;
                }
            }

        });
        exit.addKeyListener(this);
        exit.addActionListener(new ActionListener() {
            /**
             * System exit after click on button exit
             *
             * @param e button event
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });
        login.addKeyListener(this);
        login.addActionListener(new ActionListener() {
            /**
             * Try to connect to atlassian server - call login
             *
             * @param e event of connect button
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                char[] tmp = password.getPassword();
                login();
            }
        });
        getContentPane().add(username);
        getContentPane().add(password);
        getContentPane().add(login);
        getContentPane().add(exit);
    }

    /**
     * if key Esc is pressed = close app if enter is pressed = try to connect to
     * server
     *
     * @param e event of keyBoard
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if (key == KeyEvent.VK_ENTER) {
            login();
        }
    }

    /**
     * do nothing
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
        //do nothing
    }

    /**
     * do nothing
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        //do nothing
    }

    /**
     * Try to log to atlassian server and send basic request there. If it is
     * good, new window comes, if not you can try to log again
     */
    private void login() {
        char[] tmp = password.getPassword();
        int len = tmp.length;
        pass = "";
        for (int i = 0; i < len; i++) {
            pass += tmp[i];
        }
        user = username.getText();
        /*pass = "iddqdiddqd"; //I should not push this to repo, but I did...
         user = "FilipK";*/
        try {
            JiraClient jC = new JiraClient(user, pass);
            if (!jC.didAnythingReturn()) {
                JOptionPane.showMessageDialog(null, "Wrong username or password, try again.", "Wrong authentification.", JOptionPane.WARNING_MESSAGE);
            } else {
                windowManager.loginToJira(jC);
            }

            windowManager.loginToJira(jC);
        } catch (URISyntaxException ex) {
            Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "URISyntax error with connecting to jira.", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "Error in loading documents.", ex);
        } catch (JSONException ex) {
            Logger.getLogger(LoginDialogue.class.getName()).log(Level.SEVERE, "Bad identification.", ex);
        }
    }

}
