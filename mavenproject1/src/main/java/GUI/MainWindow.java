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
 *
 * @author Jituška zub
 */
public class MainWindow extends JFrame implements KeyListener{
    private JButton exit, save, refresh, shoot;
    private JLabel name, cartridges;
    private WindowManager windowManager;
    private JiraClient jiraClient;
    private Client client;
    
    public MainWindow(WindowManager w, JiraClient jc) {
        this.windowManager = w;
        this.jiraClient = jc;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");
        initComponents();
        pack();
    }
    
    public void initComponents(){
        exit = new JButton("exit");
        save = new JButton("save");
        refresh = new JButton("refresh");
        shoot = new JButton("shoot");
        
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        getContentPane().add(save);
        getContentPane().add(refresh);
        getContentPane().add(shoot);
        getContentPane().add(exit);
        
        shoot.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //shoot
            }

        });
        shoot.addKeyListener(this);
        
        save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jiraClient.safeQueue();
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
        if (key == KeyEvent.VK_A){
            client.sendMessage(new Message(Message.Command.LEFT));
        }else if(key == KeyEvent.VK_D){
            client.sendMessage(new Message(Message.Command.RIGHT));
        }else if(key == KeyEvent.VK_W){
            client.sendMessage(new Message(Message.Command.UP));
        }else if(key == KeyEvent.VK_S){
            client.sendMessage(new Message(Message.Command.DOWN));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //nothing to do
    }
}
