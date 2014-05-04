package com.project.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Client handler will be used to demonstrate an actual client requesting a connection with the applications server.
 * It will display output from the server and request the customer for a response to the appliation's dialog.
 *
 * @author Matthew McGuire
 * @version 1.2 27 April 2014
 */
public class ClientHandler implements Runnable {
    private static final int PORT = 2222;
    private static BufferedReader reader = null;
    private static PrintStream outClient = null;
    private static BufferedReader inClient = null;
    private static String toSend;
    private static String response;
    private static boolean closed = false;

    /**
     * Main method creates the socket that will attempt to connect with the server socket from the application server.
     * Also creates the other resources needed to pass streams of data back and forth over the socket between the
     * client and applcation server.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", PORT);
            socket.setKeepAlive(true);
            outClient = new PrintStream(socket.getOutputStream(), true);
            inClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            reader = new BufferedReader(new InputStreamReader(System.in));

            /*
            Create a new thread to set up infinite loop to listen for output from the server.
             */
            new Thread(new ClientHandler()).start();

            /*
            Infinite loop that responds the output from the server.
             */
            while (true) {
                toSend = reader.readLine();
                if (toSend.length() != 0) {
                    outClient.println(toSend);
                    if ((toSend.equalsIgnoreCase("quit")) || (toSend.equals("3"))) {
                        break;
                    }
                }
            }

            /*
            The customer has chosen to leave the application, close the connection.
             */
            closed = true;
            outClient.close();
            inClient.close();
            socket.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        /*
        If the connection is not closed continue to listen for output from the server.
         */
        while (!closed) {
            try {
                if (inClient.ready()) {
                    response = inClient.readLine();
                    if (response != null && response.length() != 0) {
                        System.out.println(response);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.exit(1);
            }
        }
    }
}