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
 *
 * @author Jituška zub
 */
public class ConnectDialogue extends JFrame {

    private JButton exit, connect;
    private JTextField ip, port;
    private WindowManager windowManager;
    private String ipS;
    private String adressS;
    private int portI;

    public ConnectDialogue(WindowManager w) throws HeadlessException {
        this.windowManager = w;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Connecting to server.");
        initComponents();
        pack();
    }

    public void initComponents() {
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
                portI = 1712;
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
