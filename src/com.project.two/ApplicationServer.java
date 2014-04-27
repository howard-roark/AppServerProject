package com.project.two;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class ApplicationServer {

    /**
     * Port number to listen on for connections from clients
     */
    private final int PORT = 2222;

    protected ApplicationServer() {
        try (
                ServerSocket listenSocket = new ServerSocket(PORT)
        ) {
        } catch (IOException e) {
            System.err.println("Error when initializing listenSocket: ");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
