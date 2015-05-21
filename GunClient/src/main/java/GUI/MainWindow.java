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
import java.awt.Toolkit;
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
    private JProgressBar progressBar;
    private Task task;
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
        setTitle("GunClient - Main");
        initComponents();
        pack();
    }

    /**
     * This methods init Components in frame. Add action listeners to buttons
     * exit, save, refresh and shoot, and add keyListeners to all components. By
     * pressing exit button you switch app off. By pressing save button you save
     * your profile. By pressing refresh button you get new catridges, you have
     * some. (load gun) By pressing keys ASDW you can control the gun A(move
     * left), S(move down), W(move up), D(move right), space(shoot)
     *
     */
    private void initComponents() {
        exit = new JButton("exit");
        save = new JButton("save");
        refresh = new JButton("refresh");
        shoot = new JButton("shoot");
        cartridges = new JLabel(String.valueOf(this.jiraClient.getCartridges()));
        progressBar = new JProgressBar(0, 100);

        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        getContentPane().add(cartridges);
        getContentPane().add(save);
        getContentPane().add(refresh);
        getContentPane().add(shoot);
        getContentPane().add(exit);

        progressBar.setIndeterminate(true);
        progressBar.addKeyListener(this);
        progressBar.setString("Loading new done tasks.");
        progressBar.setStringPainted(true);

        cartridges.addKeyListener(this);

        shoot.addActionListener(new acLisShoot(this));
        shoot.addKeyListener(this);

        save.addActionListener(new ActionListener() {
            /**
             * Save profile after save pressed
             *
             * @param e event of save
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                jiraClient.saveProfile();
            }

        });
        save.addKeyListener(this);
        /**
         * System exit after click on button exit
         *
         * @param e button event
         */
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });
        exit.addKeyListener(this);
        refresh.addActionListener(new ActionListener() {
            /**
             * refresh profile - load new done tasks
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                startTask();
            }

        });
        refresh.addKeyListener(this);
        this.addKeyListener(this);
    }

    /**
     * do nothing
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
        //nothing to do
    }

    /**
     * if key Esc is pressed = close app if A is pressed move gun left if S is
     * pressed move gun down if W is pressed move gun up if D is pressed move
     * gun right
     *
     * @param e event of keyBoard
     */
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
        } else if (key == KeyEvent.VK_SPACE) {
            if ("0".equals(cartridges.getText())) {
                JOptionPane.showMessageDialog(this, "You don't have any bullets left.", "Not enought bullets", JOptionPane.WARNING_MESSAGE);
            } else {
                shoot();
            }
        } else if (key == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    /**
     * do nothing
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        //nothing to do
    }

    /**
     * Class SwingWorker with doing task - load new queue of tasks
     */
    class Task extends SwingWorker<Void, Void> {

        private MainWindow mW;

        /**
         * Constructor of task
         *
         * @param mW instance of this window
         */
        public Task(MainWindow mW) {
            this.mW = mW;
        }
        /*
         * Main task. Executed in background thread.
         * Call updateIssueQueue in jiraClient
         */

        @Override
        public Void doInBackground() {
            jiraClient.updateIssueQueue();
            return null;
        }

        /*
         * Executed in event dispatching thread
         * When the task is done, button refres is enabled, progressBar removed, refres cartridges and repain Frame
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            refresh.setEnabled(true);
            getContentPane().remove(progressBar);
            cartridges.setText(Integer.toString(mW.jiraClient.getCartridges()));
            mW.repaint();
            mW.pack();
        }
    }

    /**
     * Add Action Listener for show message warning
     */
    class acLisShoot implements ActionListener {

        JFrame mW;

        public acLisShoot(JFrame mW) {
            this.mW = mW;
        }

        /**
         * When you dont have any bullets left, show warning message.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if ("0".equals(cartridges.getText())) {
                JOptionPane.showMessageDialog(mW, "You don't have any bullets left.", "Not enough bullets.", JOptionPane.WARNING_MESSAGE);
            } else {
                shoot();
            }
        }

    }

    /**
     * Shoot with gun.
     */
    private void shoot() {
        client.sendMessage(new Message(Message.Command.SHOOT));
        jiraClient.shoot();
        cartridges.setText(Integer.toString(jiraClient.getCartridges()));
        shoot.setEnabled(false);
        try {
            Thread.sleep(2000);
            shoot.setEnabled(true);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Start doing task - thread runs Add progressBar, repaint frame
     */
    private void startTask() {
        getContentPane().add(progressBar);
        refresh.setEnabled(false);
        this.repaint();
        this.pack();
        task = new Task(this);
        task.execute();
    }
}
