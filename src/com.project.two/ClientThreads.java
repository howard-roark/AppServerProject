package com.project.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by McGuireMW on 4/28/14.
 */


public class ClientThreads extends Thread {
    /**
     * Int to identify each client that is made
     */
    private int clientNum = 1;
    private String clientId;
    private PrintWriter outClient;
    private BufferedReader inClient;
    private BufferedReader reader;
    protected String threadName;

    protected ClientThreads(InetAddress localHost, int port) {
        clientId = "Client Number : " + clientNum;
        System.out.println(clientId);
        clientNum++;
        try (
                Socket socket = new Socket(localHost, port);
        ) {
            outClient = new PrintWriter(socket.getOutputStream(), true);
            inClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            reader = new BufferedReader(new InputStreamReader(System.in));
            start();
        } catch (IOException e) {
            System.err.println("Error creating client thread socket");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Please enter the string to be converted for Client " + clientId + ": ");
            String convertString = reader.readLine();
            System.out.println();
            System.out.println("Enter \'B\' to encode your String in Binary or \'H\' for Hexadecimal:");
            String format = reader.readLine();
            System.out.println();
            threadName = format + " ^ " + convertString;
            this.setName(clientId);
            outClient.println(threadName);
        } catch (IOException e) {
            System.err.println("Error running client thread");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
