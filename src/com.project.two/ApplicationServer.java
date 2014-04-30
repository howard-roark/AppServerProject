package com.project.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class ApplicationServer extends Thread {
    private static final int APP_PORT = 2222;
    private static final int DATA_SERVER_PORT = 2233;
    protected static Queue<ClientThread> clientThreads = new PriorityBlockingQueue<>();

    public static void main(String[] args) {
        try (
                ServerSocket appServer = new ServerSocket(APP_PORT);
                Socket dataServComm = new Socket(InetAddress.getLocalHost(), DATA_SERVER_PORT);
                BufferedReader inFromData = new BufferedReader(new InputStreamReader(dataServComm.getInputStream()));
        ) {
            new ApplicationServer().start();
            String response = "";
            while (true) {
                Socket clientSocket = appServer.accept();
                clientThreads.add(new ClientThread(clientSocket, dataServComm));
                response = inFromData.readLine();
                if (!response.equals("")) {
                    //TODO loop through queue to send to appropriate client by ID somehow. Need a printwriter to send need a printwriter to send response to client.
                }
            }
        } catch (IOException ioe) {
            System.err.println("Problem starting app server socket");
            ioe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unknown error starting app server");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        String request = "";
        while (true) {
            for (ClientThread thread : clientThreads) {
                request = thread.getRequest();
                if (!request.equals("")) {
                    try (
                            Socket dataSocket = thread.getSocket();
                            PrintWriter outClient = new PrintWriter(dataSocket.getOutputStream(), true);
                    ) {
                        outClient.print(request);
                    } catch (IOException ioe) {
                        System.err.println("Problem getting client socket from client thread");
                        ioe.printStackTrace();
                        System.exit(1);
                    } catch (Exception e) {
                        System.err.println("Unknown problem sending requests to data server");
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
    }
}
