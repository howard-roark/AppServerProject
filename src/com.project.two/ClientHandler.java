package com.project.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class ClientHandler implements Runnable {
    /**
     * Port in which the ApplicationServer is listening for connections
     */
    private static final int PORT = 2222;

    private static BufferedReader reader = null;
    private static Socket socket = null;
    private static PrintStream outClient = null;
    private static BufferedReader inClient = null;
    private static String toSend;
    private static String response;
    private static boolean closed = false;

    public static void main(String[] args) {
        try {
//            try {
//            System.out.println("Welcome to Dr. Dog's Veterinary Clinic's State of the Art Scheduling App!");
//            System.out.println("\tPlease choose from the following options:");
//            System.out.println("\t\t0: See Available Time Slots\n\t\t1: Confirm your chosen time slot\n\t\t2: Exit");
//            choice = reader.readLine();
//            while ((!choice.equals("0")) && (!choice.equals("1")) && (!choice.equals("2"))) {
//                System.out.println("Please choose a valid option: 0, 1 or 2");
//                choice = reader.readLine();
//            }
            
            socket = new Socket("localhost", PORT);
            socket.setKeepAlive(true);
            outClient = new PrintStream(socket.getOutputStream(), true);
            inClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            reader = new BufferedReader(new InputStreamReader(System.in));

            new Thread(new ClientHandler()).start();
            
            while (true) {
                toSend = reader.readLine();
                if(toSend.length() != 0) {
                    if(toSend.equalsIgnoreCase("quit")) {
                        break;
                    }
                    outClient.println(toSend);
                }
            }
            
            closed = true;
            outClient.close();
            inClient.close();
            socket.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

    @Override
    public void run() {
        while(!closed) {
            try {
                if(inClient.ready()) {
                    response = inClient.readLine();
                    if(response!=null && response.length()!=0) {
                        System.out.println(response);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}