package com.project.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Matthew McGuire
 * @version 0.3 27 April 2014
 */

public class ClientThread extends Thread {
    private Socket clientSocket;
    private Socket dataSocket;
    private String request;

    protected ClientThread(Socket appServerConnection, Socket dataSocket) {
        this.clientSocket = appServerConnection;
        this.dataSocket = dataSocket;
    }

    public void run() {
        try (
                BufferedReader inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            request = inClient.readLine();
        } catch (IOException ioe) {
            System.err.println("Problem creating readers / writers");
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    protected String getRequest() {
        return request;
    }

    protected Socket getSocket() {
        return this.dataSocket;
    }
}
