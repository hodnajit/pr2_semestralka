/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gunserver;

import comm.Server;
import java.util.Scanner;

/**
 *
 * @author Jituška zub
 */
public class GunServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
    	
        Server s = new Server();
        s.setResetOnNewUser(false);
        Scanner scanner = new Scanner(System.in);
        s.run();
        while(true){
            String command = scanner.next();
            System.out.println(command);
            if(command == "exit"){
                s.stop();
                System.exit(0);
            }else{
                System.out.println("Unknown command!");
            }
        }
    }
    
}
