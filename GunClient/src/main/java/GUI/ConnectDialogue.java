/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Service.WindowManager;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

/**
 * This Class is window (JFrame) for connecting to server, that runs on rpi. You
 * add IP adress of rpi and Port where it listen and then connect by button
 * connect. You can also switch application by pressing button exit. It extends
 * JFrame.
 *
 * @author Jituška zub
 */
public class ConnectDialogue extends JFrame implements KeyListener {

    /**
     * Buttons exit and connect
     */
    private JButton exit, connect;
    /**
     * Text Fields ip and port.
     */
    private JTextField ip, port;
    /**
     * WindowManager to manage all windows in application
     */
    private WindowManager windowManager;
    /**
     * IP address of server, string for creating client
     */
    /**
     * If is clicked on TextField IP
     */
    private boolean ipClicked = false;
    /**
     * If is clicked on TextField PORT
     */
    private boolean portClicked = false;

    /**
     * Constructor of ConnectDialogue window, needs windowManager to manage
     * window, it call method initComponents() to init components in frame
     *
     * @param w window manager
     * @throws HeadlessException
     */
    public ConnectDialogue(WindowManager w) throws HeadlessException {
        this.windowManager = w;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GunClient - connecting to server");
        initComponents();
        pack();
    }

    /**
     * Init components, add action listeners to them, buttons exit and connect,
     * key listeners to frame and all components respond when buttons are
     * pressed
     */
    private void initComponents() {
        ip = new JTextField("IP adress", 20);
        connect = new JButton("connect");
        port = new JTextField("Port", 5);
        exit = new JButton("exit");

        ip.addKeyListener(this);
        ip.addFocusListener(new FocusListener() {
            /**
             * Modify FocusListener - after clicked on TextField, ipClicked =
             * true
             *
             * @param e event of TextField ip
             */
            @Override
            public void focusGained(FocusEvent e) {
                if (!ipClicked) {
                    ip.setText("");
                    ipClicked = true;
                }
            }

            /**
             * After focus Lost - if it is still empty, set ip text on "IP
             * adress" and ipClicked = false;
             *
             * @param e event of textfield
             */
            @Override
            public void focusLost(FocusEvent e) {
                if ("".equals(ip.getText())) {
                    ip.setText("IP adress");
                    ipClicked = false;
                }
            }

        });
        port.addKeyListener(this);
        port.addFocusListener(new FocusListener() {
            /**
             * Modify FocusListener - after clicked on TextField, portClicked =
             * true
             *
             * @param e event of TextField port
             */
            @Override
            public void focusGained(FocusEvent e) {
                if (!portClicked) {
                    port.setText("");
                    portClicked = true;
                }
            }

            /**
             * After focus Lost - if it is still empty, set ip text on "port"
             * and portClicked = false;
             *
             * @param e event of textfield
             */
            @Override
            public void focusLost(FocusEvent e) {
                if ("".equals(port.getText())) {
                    port.setText("Port");
                    portClicked = false;
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
        connect.addKeyListener(this);
        connect.addActionListener(new ActionListener() {
            /**
             * Try to connect to server - call connectToClient in windowManager
             *
             * @param e event of connect button
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                windowManager.connectToClient(ip.getText(), Integer.parseInt(port.getText()));
                dispose();
            }

        });
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        getContentPane().add(ip);
        getContentPane().add(port);
        getContentPane().add(connect);
        getContentPane().add(exit);

    }

    /**
     * Do nothing
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
        //do nothing
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
            windowManager.connectToClient(ip.getText(), Integer.parseInt(port.getText()));
            dispose();
        }
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
}
