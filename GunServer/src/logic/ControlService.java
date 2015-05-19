/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import java.util.Timer;
import java.util.TimerTask;
import model.Message;

/**
 * Control service manipulates with servos, that are on two pins
 * @author Jituška zub
 */
public class ControlService {    
    final Serial serial = SerialFactory.createInstance();
    final GpioController gpio = GpioFactory.getInstance();
    /**
     * pin to drive the gun
     */
    final GpioPinDigitalOutput pinDrive;
    /**
     * pin where trigger is
     */
    final GpioPinDigitalOutput pinTrigger;
    private Timer timer;
    /**
     * Maximum angle how servo can be
     */
    private int maxAngle = 400;
    /**
     * Minimum angle that servo can be
     */
    private int minAngle = -400;
    /**
     * Angle to turn in command case UP, DOWN, LEFT, RIGHT, it inicialized on twentieth from range of servo
     */
    private int stepAngle = (maxAngle-minAngle)/20;
    /**
     * Actual horizontal position of servo, inicialized on default position = 0;
     */
    private int horPosition=0;
    /**
     * Actual vertical position of servo, inicialized on default position = 0;
     */
    private int verPosition=0;
    /**
     * Construct control service, set pinDrive and pinTrigger
     */
    public ControlService(){
        this.pinDrive = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "PinDrive", PinState.HIGH);
        this.pinDrive.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);
        this.pinTrigger = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "PinDrive", PinState.HIGH);
        this.pinTrigger.setShutdownOptions(true, PinState.HIGH, PinPullResistance.OFF);
        serial.open(Serial.DEFAULT_COM_PORT, 9600);
        timer = new Timer();
    }
    /**
     * Execute command which client write, it can turn horangle and verangle on angle, which client enter, or turn default on stepAngle left, right, up and down
     * @param command the desired command from client
     * @param cmdValue value of that command
     */
    public void executeCommand(Message.Command command, int cmdValue) {
        int angle;
        switch (command) {
            case HORANGLE:                
                horPosition=cmdValue;
                if (horPosition>maxAngle){
                    horPosition=maxAngle;
                    serial.write("s0 "+horPosition+" 0\n");
                }else if(horPosition<minAngle){
                    horPosition=minAngle;
                    serial.write("s0 "+horPosition+" 0\n");
                }else{
                    serial.write("s0 "+horPosition+" 0\n");
                }
                break;
            case VERANGLE:
                verPosition=cmdValue;
                if (verPosition>maxAngle){
                    verPosition=maxAngle;
                    serial.write("s1 "+verPosition+" 0\n");
                }else if(verPosition<minAngle){
                    verPosition=minAngle;
                    serial.write("s1 "+verPosition+" 0\n");
                }else{
                    serial.write("s1 "+verPosition+" 0\n");
                }
                break;
            case UP:
                angle=stepAngle;
                verPosition+=stepAngle;
                if (verPosition>maxAngle){
                    verPosition=maxAngle;
                    serial.write("s1 "+horPosition+" 0\n");
                }else if(verPosition<minAngle){
                    verPosition=minAngle;
                    serial.write("s1 "+horPosition+" 0\n");
                }else{                    
                    serial.write("s1 "+horPosition+" 0\n");
                }
                break;
            case DOWN:
                angle=-stepAngle;
                verPosition+=angle;
                if (verPosition>maxAngle){
                    verPosition=maxAngle;
                    serial.write("s1 "+horPosition+" 0\n");
                }else if(verPosition<minAngle){
                    verPosition=minAngle;
                    serial.write("s1 "+horPosition+" 0\n");
                }else{                    
                    serial.write("s1 "+horPosition+" 0\n");
                }
                break;
            case RIGHT:
                angle=stepAngle;
                horPosition+=angle;
                if (horPosition>maxAngle){
                    horPosition=maxAngle;
                    serial.write("s0 "+horPosition+" 0\n");
                }else if(horPosition<minAngle){
                    horPosition=minAngle;
                    serial.write("s0 "+horPosition+" 0\n");
                }else{                    
                    serial.write("s0 "+horPosition+" 0\n");
                }
                break;
            case LEFT:
                angle=-stepAngle;
                horPosition+=angle;
                if (horPosition>maxAngle){
                    horPosition=maxAngle;
                    serial.write("s0 "+horPosition+" 0\n");
                }else if(horPosition<minAngle){
                    horPosition=minAngle;
                    serial.write("s0 "+horPosition+" 0\n");
                }else{                    
                    serial.write("s0 "+horPosition+" 0\n");
                }
                break;
            case SHOOT:
                pinDrive.low();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        pinTrigger.low();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                pinTrigger.high();
                                pinDrive.high();
                            }
                        }, 500);
                    }
                }, 3000);
                break;
            default:
            
        }
    }
    
    /**
     * It reset position of each servos
     */
    public void resetPosition() {
        serial.write("sa\n");
    }
    
}
