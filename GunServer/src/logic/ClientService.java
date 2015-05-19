/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import comm.Server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import model.Message;

/**
 * Service that provides communication with client - serves his request
 * @author Jituška zub
 */
public class ClientService implements Runnable{
    
    final boolean verbose = true;
    /**
     * socket on server
     */
    private Socket s;
    private boolean shouldRun = true;
    /**
     * Id is id of client which service is it
     */
    public int id;
    private ControlService controlService;
    private boolean sendAllowedToControlMessage = false;
    private boolean sendDisallowedToControlMessage = false;
    private boolean allowedToControl = false;
    private boolean pingReceived = true;
    private Server server;
    private Timer timer;
    
    /**
     * Construct of client service
     * @param s is socket on server, where it listen the client
     * @param id is id of the client
     * @param controlService instance of control service
     * @param server server that client wants to communicate
     */
    public ClientService(Socket s, int id, ControlService controlService, Server server){
        this.s = s;
        this.id = id;
        this.controlService = controlService;
        this.server = server;
        
    }
    /**
     * Set true sendAllowToControlMessage or sendDisallowedToControlMessage depend on allowed
     * @param allowed is true if the client is allowed to remote the gun and false if not
     */
    public void allowToRun(boolean allowed){
        this.allowedToControl = allowed;
        if(allowed){
            this.sendAllowedToControlMessage = true;
        }else{
            this.sendDisallowedToControlMessage = true;
        }
    }
    /**
     * 
     */
    private class PingTask extends TimerTask{
        private ClientService cs;
        public PingTask(ClientService cs){
            this.cs = cs;
        }
        
        @Override
        public void run() {
            
            if(cs.pingReceived){
                cs.pingReceived = false;
            }else{
                System.out.println("I: Ping not received in time from client "+id);
                cs.timer.cancel();
                cs.timer.purge();
                cs.shouldRun = false;
            }
        }
    }
    /**
     * Check if it gets ping in time or not
     */
    private synchronized void checkPing(){
        if(pingReceived){
            setPing(false);
        }else{
            System.out.println("I: Ping not received in time from client "+id);
            timer.cancel();
            timer.purge();
            shouldRun = false;
        }
    }
    /**
     * Set pingReceived
     * @param ping is true if it gets ping and false if not
     */
    private synchronized void setPing(boolean ping){
        this.pingReceived = ping;
    }
    /**
     * If client is allowed to remote the gun, it starts to write messages to server, messages that writes client. Close streams, when client is not already
     * allowed to remote the gun
     * @throws IOException when there is a problem with opening streams
     * @throws ClassNotFoundException when client writes message that does not exist
     */
    @Override
    public void run() {
        ObjectOutputStream os = null;
        ObjectInputStream is = null;
        try {
            os = new ObjectOutputStream(s.getOutputStream());
            is = new ObjectInputStream(s.getInputStream());
            timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask(){
                    @Override
                    public void run() {
                        checkPing();
                    }
                }, 5000, 5000);
            while(shouldRun){
                Message msg = (Message) is.readObject();
                switch (msg.type) {
                    case HANDSHAKE:
                        os.writeObject(new Message(Message.Type.INFO, "Hey what's up?"));
                        System.out.println("I: Handshake received from "+ id);
                        break;
                    case FAREWELL:
                        os.writeObject(new Message(Message.Type.FAREWELL, "Fuck you too, bro."));
                        System.out.println("I: Client " + id + " says bye.");
                        shouldRun = false;
                        break;
                    case ERROR:
                        os.writeObject(new Message(Message.Type.FAREWELL, "Problem? GTFO."));
                        System.out.println("I: Client " + id + " has problems: " + msg.message + " Dumping him.");
                        shouldRun = false;
                        break;
                    case COMMAND:
                        if(this.allowedToControl){
                            this.controlService.executeCommand(msg.command, msg.cmdValue);
                            if(verbose) System.out.println("I: Command received from client "+id);
                        }else{
                            os.writeObject(new Message(Message.Type.ERROR, "You have no power here."));
                            System.out.println("I: Client " + id + " is trying to control although not allowed to.");
                        }
                        break;
                    case PING:
                        if(verbose) System.out.println("I: Ping received from client "+id);
                        setPing(true);
                        break;
                    default:
                        // Possibly info. Why is cliend sending info? Fuck him.
                }
                if(sendAllowedToControlMessage || sendDisallowedToControlMessage){
                    os.writeObject(new Message(Message.Type.INFO, sendAllowedToControlMessage ? "Allowed to control" : "Disallowed control"));
                }
                os.flush();
            }
        } catch (IOException ex) {
            System.err.println("E: Client " + id + " comms fucked up while opening I/O streams");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.err.println("E: Client " + id + " sending weird messages. Dumping him.");
        } finally {
            try {
                os.close();
                is.close();
            } catch (IOException ex) {
                System.err.println("E: Sir you are fucked, error while closing I/O streams for " + id + ". Restart everything and weep.");
            }finally{
                this.server.bye(this.id);
            }
        }
        
    }
    
}
