/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.Message;

/**
 *
 * @author Jituška Zub
 */
public class Client implements Runnable{
    /**
     * Adress of server on IP adress
     */
    private String host;
    /**
     * Port of server
     */
    private int port;   
    /**
     * Socket of client, where client listen
     */
    private Socket s;
    private boolean shouldRun = true;
    private Timer pingTimer;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private boolean startedUp = false;
    private Queue<Message> queue;
    /**
     * Construct of client
     */
    public Client(String host, int port){
        this.host = host;
        this.port = port;
        queue = new LinkedList<Message>();
        //System.out.println("Construct client.");
    }
    /**
     * Starts running of client. Make socket to listen the server, send ping every 2ms, and send other messages, that are added to queue
     * , after all close the socket
     * @UnknownHostException if it cannot find server
     * @IOException if there is some I/O problem or socket close failed
     * @InterruptedException if 
     */
    @Override
    public void run(){
        //System.out.println("Run client.");
        try {
            s = new Socket(host, port);
        } catch (UnknownHostException ex) {
            System.err.println("E: Nothing fucking found @ " + host + ":" + port);
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, "Unknown ip adress or port.", "UnknownHostException", JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("E: Fucking I/O problem");
            JFrame jframe = new JFrame();
            JOptionPane.showMessageDialog(jframe, "IO Problem while connecting to server.", "IO problem", JOptionPane.ERROR_MESSAGE);  
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            //System.out.println("Ping.");
            os = new ObjectOutputStream(s.getOutputStream());
            is = new ObjectInputStream(s.getInputStream());
            os.writeObject(new Message(Message.Type.HANDSHAKE, "Hey!"));
            os.flush();
            pingTimer = new Timer();
            pingTimer.scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run() {
                    try {
                        os.writeObject(new Message(Message.Type.PING, "PU = Ping you."));
                        os.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                
                }, 2000, 2000);
            this.startedUp = true;
            while(shouldRun){
                try {
                    while(!queue.isEmpty()){
                        os.writeObject(queue.poll());
                        os.flush();
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    JFrame jframe = new JFrame();
                    JOptionPane.showMessageDialog(jframe, "Sending request interrupted.", "Interrupted problem.", JOptionPane.ERROR_MESSAGE);    
                }
                
            }
        }catch(IOException ex){
            System.err.println("E: Something's fucked up. Deal with it.");
            JFrame jframe = new JFrame();
            JOptionPane.showMessageDialog(jframe, "IO Problem while sending your request.", "IO problem.", JOptionPane.ERROR_MESSAGE);         
            ex.printStackTrace();
        } finally{
            try {
                pingTimer.cancel();
                pingTimer.purge();
                s.close();
            } catch (IOException ex) {
                System.err.println("E: Socket close failed, sucker!");
            }
        }
    }
    
    public boolean succesfullyStarted(){
        return this.startedUp;
    }
    
    public void sendMessage(Message messageToSend){
        this.queue.add(messageToSend);
    }
    
}
