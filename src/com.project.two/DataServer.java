package com.project.two;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * DataServer will listen for connections from ApplicationServer and decide whether to translate string from client into
 * Binary or Hexadecimal.
 *
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class DataServer {

    /**
     * Port to listen for connection to DataServer from ApplicationServer
     */
    private final int PORT = 2233;

    protected DataServer() {
        try (
                ServerSocket dataListener = new ServerSocket(PORT);
        ) {
        } catch (IOException e) {
            System.err.println("Error when initializing dataListener: ");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
