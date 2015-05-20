/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Service.WindowManager;
import comm.Client;
import comm.JiraClient;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import model.Message;

/**
 * This Class is main window (JFrame) in application. Here you can control
 * moving of gun with keys ASDW and shoot by button shoot. You can also save
 * your profile or refresh to get new cartridges. You can switch the application
 * off as well. It extends JFrame and implements KeyListener
 *
 * @author Jituška zub
 */
public class MainWindow extends JFrame implements KeyListener {

    private JButton exit, save, refresh, shoot;
    private WindowManager windowManager;
    private JLabel cartridges;
    /**
     * User's jiraClient
     */
    private JiraClient jiraClient;
    /**
     * User's client to communicate with server
     */
    private Client client;

    /**
     * Constructor of LoginDialogue window, needs windowManager to manage
     * window, it call method initComponents() to init components in frame. Need
     * three arguments.
     *
     * @param w windowManager that manages windows in app
     * @param jc jiraClient of user
     * @param c client of user (for communication with server
     */
    public MainWindow(WindowManager w, JiraClient jc, Client c) {
        this.windowManager = w;
        this.jiraClient = jc;
        this.client = c;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");
        initComponents();
        pack();
    }

    /**
     * This methods init Components in frame. Add action listeners to buttons
     * exit, save, refresh and shoot, and add keyListeners to all components. By
     * pressing exit button you switch app off. By pressing save button you save
     * your profile. By pressing refresh button you get new catridges, you have
     * some. (load gun) By pressing keys ASDW you can control the gun A(move
     * left), S(move down), W(move up), D(move right)
     *
     */
    private void initComponents() {
        exit = new JButton("exit");
        save = new JButton("save");
        refresh = new JButton("refresh");
        shoot = new JButton("shoot");
        cartridges = new JLabel(String.valueOf(this.jiraClient.getCartridges()));

        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        getContentPane().add(cartridges);
        getContentPane().add(save);
        getContentPane().add(refresh);
        getContentPane().add(shoot);
        getContentPane().add(exit);

        cartridges.addKeyListener(this);
        shoot.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMessage(new Message(Message.Command.SHOOT));
            }

        });
        shoot.addKeyListener(this);

        save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jiraClient.saveProfile();
            }

        });
        save.addKeyListener(this);
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });
        exit.addKeyListener(this);
        refresh.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jiraClient.updateIssueQueue();
            }

        });
        refresh.addKeyListener(this);
        this.addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //nothing to do
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A) {
            client.sendMessage(new Message(Message.Command.LEFT));
            //System.out.println("left");
        } else if (key == KeyEvent.VK_D) {
            client.sendMessage(new Message(Message.Command.RIGHT));
            //System.out.println("right");
        } else if (key == KeyEvent.VK_W) {
            //System.out.println("up");
            client.sendMessage(new Message(Message.Command.UP));
        } else if (key == KeyEvent.VK_S) {
            //System.out.println("down");
            client.sendMessage(new Message(Message.Command.DOWN));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //nothing to do
    }
}
