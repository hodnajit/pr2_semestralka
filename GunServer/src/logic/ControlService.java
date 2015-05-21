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
     * Maximum vertical angle how servo can be
     */
    private final int maxAngleVer = 1000;
    /**
     * Minimum vertical angle that servo can be
     */
    private final int minAngleVer = -500;
    /**
     * Maximum vertical angle how servo can be
     */
    private final int maxAngleHor = 1000;
    /**
     * Minimum vertical angle that servo can be
     */
    private final int minAngleHor = -1500;
    /**
     * Angle to turn in command case UP, DOWN, LEFT, RIGHT, it initialized an one twentieth from range of servo (also, it is not)
     */
    private int stepAngleVer = 200;
    private int stepAngleHor = 200;
    /**
     * Actual horizontal position of servo, initialized on default position = -350;
     */
    private int horPosition = -350;
    /**
     * Actual vertical position of servo, initialized on default position = 400;
     */
    private int verPosition = 400;
    /**
     * Default Vertical angle for servos, initialized 400
     */
    private final int defaultVer = 400;
    /**
     * Default horizontal angle for servos, initialized -350
     */
    private final int defaultHor = -350;
    private int fast = 20; //?
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
    
    private enum Angle{
        HORIZONTAL,
        VERTICAL
    }
    
    //helper method to saturate angle
    private void alterAngleSaturated(int delta, Angle angle){
        if(angle == Angle.HORIZONTAL){
            horPosition += delta;
            if (horPosition > maxAngleHor){
                horPosition = maxAngleHor;
            }else if(horPosition < minAngleHor){
                horPosition = minAngleHor;
            }
        }else{
            verPosition += delta;
            if (verPosition > maxAngleVer){
                verPosition = maxAngleVer;
            }else if(verPosition < minAngleVer){
                verPosition = minAngleVer;
            }
        }
    }
    
    /**
     * Execute command enclosed in message
     * @param command command type to execute
     * @param cmdValue command value
     */
    public void executeCommand(Message.Command command, int cmdValue) {
        switch (command) {
            /*
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
            */
            case UP:
                this.alterAngleSaturated(stepAngleVer, Angle.VERTICAL);
                serial.write("s0 "+verPosition+" "+fast+"\n");
                serial.flush();
                break;
            case DOWN:
                this.alterAngleSaturated(-stepAngleVer, Angle.VERTICAL);
                serial.write("s0 "+verPosition+" "+fast+"\n");
                serial.flush();
                break;
            case RIGHT:
                this.alterAngleSaturated(stepAngleHor, Angle.HORIZONTAL);
                serial.write("s1 "+horPosition+" "+fast+"\n");
                break;
            case LEFT:
                this.alterAngleSaturated(-stepAngleHor, Angle.HORIZONTAL);
                serial.write("s1 "+horPosition+" "+fast+"\n");
                serial.flush();
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
                        }, 100);
                    }
                }, 1000);
                break;
            default:
            
        }
    }
    
    /**
     * It reset position of each servos
     */
    public void resetPosition() {
        serial.write("sa "+defaultVer+" "+defaultVer+"\n");
    }
    
}
