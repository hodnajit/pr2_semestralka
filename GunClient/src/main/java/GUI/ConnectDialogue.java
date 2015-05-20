/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Service.WindowManager;
import comm.Client;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * This Class is window (JFrame) for connecting to server, that runs on rpi. You
 * add IP adress of rpi and Port where it listen and then connect by button
 * connect. You can also switch application by pressing button exit. It extends
 * JFrame.
 *
 * @author Jituška zub
 */
public class ConnectDialogue extends JFrame {

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
     * IP adress of server, string for creating client
     */
    private String ipS;
    /**
     * Port, where rpi listen
     *
     */
    private int portI;

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
        setTitle("Connecting to server.");
        initComponents();
        pack();
    }

    /**
     * Init components, add action listeners to them, buttons exit and connect
     * respond when buttons are pressed
     */
    private void initComponents() {
        ip = new JTextField("IP adress", 20);
        connect = new JButton("connect");
        port = new JTextField("Port", 10);
        exit = new JButton("exit");
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });
        connect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ipS = ip.getText();
                //portI = Integer.parseInt(port.getText());
                //to tu pak nebude - test
                ipS = "10.0.0.3";
                //portI = 1712;
                Client c = new Client(ipS, portI);
                Thread t = new Thread(c);
                t.setDaemon(true);
                t.start();
                windowManager.connectedToClient(c);
            }

        });
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        getContentPane().add(ip);
        getContentPane().add(port);
        getContentPane().add(connect);
        getContentPane().add(exit);

    }
}
