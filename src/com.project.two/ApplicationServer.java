package com.project.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class ApplicationServer {

    /**
     * Port number to listen on for connections from clients
     */
    private static final int PORT = 2222;
    /**
     * Port number that DataServer is listening on
     */
    private static final int DATA_SERVER_PORT = 2233;

    public static void main(String[] args) {
        Socket client;
        try (
                ServerSocket listenSocket = new ServerSocket(PORT);
                Socket clientToData = new Socket(InetAddress.getLocalHost(), DATA_SERVER_PORT);
        ) {
            while (true) {
                client = listenSocket.accept();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                    BufferedReader toData = in;
                    PrintWriter fromData = out;
                    new DataServer(toData, fromData, clientToData);
                } catch (Exception e) {
                    System.err.println("Error when connecting to data server");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error when initializing listenSocket: ");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
