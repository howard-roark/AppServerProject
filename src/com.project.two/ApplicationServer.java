package com.project.two;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class ApplicationServer extends Thread {

    /**
     * Port number to listen on for connections from clients
     */
    private static final int PORT = 2222;

    public static void main(String[] args) {
        try (
                ServerSocket listenSocket = new ServerSocket(PORT);
        ) {
            Socket client = listenSocket.accept();
            System.out.println("Connection made");
            DataInputStream in = new DataInputStream(client.getInputStream());
            OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream());
            String line;
            while (true) {
                line = in.readUTF();
                System.out.println(line);
                out.write("YOUR ENCODING HAS NOT BEEN HANDLED YET STILL NEED TO GET DATA SERVER UP");
                String[] getFirstChar = line.split("^");
                if (getFirstChar[0].equals("B")) {
                    System.out.println("encode in Binary");
                } else {
                    System.out.println("Encode in Hexadecimal");
                }
            }
        } catch (IOException e) {
            System.err.println("Error when initializing listenSocket: ");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
