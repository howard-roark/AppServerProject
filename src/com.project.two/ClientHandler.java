package com.project.two;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class ClientHandler {
    /**
     * Port in which the ApplicationServer is listening for connections
     */
    private static final int PORT = 2222;

    public static void main(String[] args) {
        while (true) {
            try {
                new ClientThreads(InetAddress.getLocalHost(), PORT);
            } catch (UnknownHostException e) {
                System.err.println("Local host unknown");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}