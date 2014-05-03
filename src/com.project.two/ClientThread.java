package com.project.two;

import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * @author Matthew McGuire
 * @version 1.0 27 April 2014
 */

public class ClientThread extends Thread {
    protected static Socket clientSocket = null;
    private String request;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public ClientThread(Socket appServerConnection, String[] timeSlots, Map<String, String> appointments) {
        try {
            this.clientSocket = appServerConnection;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
        } catch (Exception e) {
            System.err.println("Problem creating resources for thread");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        try {
            String choice = in.readLine();
            if (choice.equals("0")) {
                System.out.println("Finding available time slots...");
                String timeSlot = "";
                synchronized (this) {
                    for (int i = 0; i < ApplicationServer.timeSlots.length; i++) {
                        timeSlot = ApplicationServer.timeSlots[i];
                        out.println(i + ": " + timeSlot);
                    }
                }
            } else if (choice.equals("1")) {
                System.out.println("Looking up current appointments...");
                out.print("Please enter your name: ");

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                this.clientSocket.close();
                this.in.close();
                this.out.close();
            } catch (IOException ioe) {
                System.err.println("Problem closing resources");
                ioe.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                System.err.println("Unknown error closing resources");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public String toString() {
        return "";
    }
}
