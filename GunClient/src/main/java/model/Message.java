/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Jituška zub
 */
public class Message implements Serializable {

    /**
     * Type of message that can server get and send
     */
    public enum Type {

        /**
         * Handshake when communication is established
         */
        HANDSHAKE,
        /**
         * Farewell when communication is ended
         */
        FAREWELL,
        /**
         * Command when it gets command
         */
        COMMAND,
        /**
         * Info to write info to client
         */
        INFO,
        /**
         * Error to send and get info about errors
         */
        ERROR,
        /**
         * Ping to send and get ping
         */
        PING
    }

    /**
     * Type of command that can server get
     */
    public enum Command {

        /**
         * Up to move up with gun
         */
        UP,
        /**
         * right to move the gun right
         */
        RIGHT,
        /**
         * down to move the gun down
         */
        DOWN,
        /**
         * left to move the gunt left
         */
        LEFT,
        /**
         * horangle to move the gun somewhere horizontal
         */
        HORANGLE,
        /**
         * verangle to move the gun somewhere verizontal
         */
        VERANGLE,
        /**
         * Shoot to shoot
         */
        SHOOT
    }
    /**
     * Type of the message
     */
    public Type type;
    /**
     * Contend of the message
     */
    public String message;
    /**
     * Command of the message
     */
    public Command command;
    /**
     * Value of the command
     */
    public int cmdValue;

    /**
     * Constructor to create message of enter type and string
     *
     * @param type type of the message
     * @param message contend of message
     */
    public Message(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * Constructor to create message as the entered command
     *
     * @param cmd command of the message
     */
    public Message(Command cmd) {
        this.type = Type.COMMAND;
        this.command = cmd;
    }

    /**
     * Constructor to create message as the entered command and value of command
     *
     * @param cmd command of the message
     * @param cmdValue value of the command
     */
    public Message(Command cmd, int cmdValue) {
        this.type = Type.COMMAND;
        this.command = cmd;
        this.cmdValue = cmdValue;
    }
}
