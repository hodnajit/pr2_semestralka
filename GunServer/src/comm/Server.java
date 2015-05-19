/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import logic.ClientService;
import logic.ControlService;

/**
 * Server which runs on rpi
 * Password to rpi: veslo12
 * sudo java -jar /home/pi/NetBeansProjects/gunSer/dist/gunSer.jar
 * @author Jituška zub
 */
public class Server implements Runnable{    
    /**
     * Initialized port of this server
     */
    private final int port = 1712;    
    /**
     * Initialized if the guns hsould reset the position, when the client is log out.
     */
    private boolean resetOnNewUser = true;
    /**
     * Socket on the server where the client listen and write
     */
    private static ServerSocket ss;
    /**
     * Should run is inicialized on true, first cliend should remote the gun
     */
    private boolean shouldRun = true;
    /**
     * UID is unic indentification of each client, inizialized 1
     */
    private int uid = 1;
    /**
     * Clients is array of all connected clients
     */
    private ArrayList<ClientService> clients;
    /**
     * runningId is id of client who is remoting the gun
     */
    private int runningId = -1;
    /**
     * control is instace of ControlService
     */
    private ControlService control;    
    /**
     * Construct a server which runs on rpi, create also array list of connected clients
     * 
    */
    public Server() {
        clients = new ArrayList<ClientService>();
    }
    /**
     * Tests if it should or should not reset position of gun on new user
     * @param resetOnNewUser is true when it should reset and false when not
     */
    public void setResetOnNewUser(boolean resetOnNewUser) {
        this.resetOnNewUser = resetOnNewUser;
    }    
    
    /**
     * Create instance of class ControlService and set all servos to default position. If it should run, create socket, connect client, which has this socket,
     * his id, this control service and it runs on this server, create new thread with that client and put him to list of clients and start this thread. If is
     * only one client in our list, it should runs, or it waits till clients log out
     * @throws UnknownHostException
     * @throws IOException
     * @throws SocketException
     * @throws SocketTimeoutException
     * 
    */    
    @Override
    public void run() {
        control = new ControlService();
        control.resetPosition();
        try {
            ss = new ServerSocket(port);
            System.out.println("Gun server running @ " + InetAddress.getLocalHost() + ":" + port);
        } catch (UnknownHostException ex) {
            System.err.println("E: Fuck I have no idea where I am (but I am running quite ok)");
        } catch (IOException ex) {
            System.err.println("E: Error while fucking with ports on your RPi");
            System.exit(0);
        }
        try {
            ss.setSoTimeout(1000);
        } catch (SocketException ex) {
            //should not occure
        }
        Socket s;
        Thread t;
        while (shouldRun) {
            try {
                s = ss.accept();
                int id = this.uid++;
                ClientService client = new ClientService(s, id, control, this);
                t = new Thread(client);
                clients.add(client);
                t.setDaemon(true);
                t.start();
                if(clients.size() == 1) client.allowToRun(true);
            } catch (SocketTimeoutException e){ 
            } catch (IOException ex) {
                System.err.println("E: Client communication too fucked up to accept");
                ex.printStackTrace();
            }
        }
    }
    /**
     * Close server
     * @throws IOException if thread is blocked and it cannot close server
     */
    public void stop(){
        this.shouldRun = false;
        try {
            ss.close();
        } catch (IOException ex) {
            System.err.println("Thread blocked, can't stop server socket.");
        }
    }
    /**
     * Log out client and let next one to remote the gun
     * @param id the id of client, which wants to log out
     */
    public void bye(int id) {
        ClientService minClient = null;
        if(runningId == id){
            // Shutting down controller, telling next in line he may control.
            for (Iterator<ClientService> it = this.clients.iterator(); it.hasNext();) {
                ClientService client = it.next();
                if(client.id != id){
                    if(minClient == null || client.id < minClient.id){
                        minClient = client;
                    }
                }else{
                    client.allowToRun(false);
                    clients.remove(client);
                    control.resetPosition();
                }
            }
            if(minClient != null){
                minClient.allowToRun(true);
            }
        }else{
            // Shutting down non-controller
            for(ClientService client : clients){
                if(client.id == id){
                    clients.remove(client);
                    break;
                }
            }
        }
        if(resetOnNewUser) control.resetPosition();
    }
}
