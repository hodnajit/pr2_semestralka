/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import comm.JiraClient;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Jituška zub
 */
public class ProgressBarLoading extends JPanel {

    private static JiraClient jc;
    private Task task;
    public final JProgressBar progressBar;
    private static JFrame frame = new JFrame("ProgressBar");

    class Task extends SwingWorker<Void, Void> {

        /*
         * Main task. Executed in background thread.
         */

        @Override
        public Void doInBackground() {
            jc.updateIssueQueue();
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            setCursor(null); //turn off the wait cursor
            frame.dispose();
        }
    }

    public ProgressBarLoading(JiraClient jiraClient) {
        this.jc = jiraClient;
        progressBar = new JProgressBar(0, 100);
        JPanel panel = new JPanel();
        panel.add(progressBar);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(panel, BorderLayout.PAGE_START);
    }

    public static void createAndShow(ProgressBarLoading pbl) {
        //Create and set up the window.
        System.out.println("create");
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = pbl;
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        pbl.startTask(frame);
    }
    private void startTask(JFrame frame){        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        task = new Task();
        task.execute();        
    }
}
