package com.project.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthew McGuire
 * @version 1.0 27 April 2014
 */
public class ApplicationServer {
    private static final int PORT = 2222;
    protected static String[] timeSlots = new String[20];
    protected static Map<String, String> bookedAppointments = new HashMap<>();
    private static ServerSocket appServer = null;
    private static Socket clientSocket = null;

    public static void main(String[] args) {
        buildTimeSlots();
        buildAppointments();
        try {
            appServer = new ServerSocket(PORT);
            while (true) {
                clientSocket = appServer.accept();
                new Thread(new ClientThread(clientSocket, timeSlots, bookedAppointments));
            }
        } catch (IOException ioe) {
            System.err.println("Problem starting app server socket");
            ioe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unknown error starting app server");
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                System.out.println("Closing clientSocket...");
                clientSocket.close();
            } catch (IOException ioe) {
                System.err.println("Problem closing server socket");
                ioe.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                System.err.println("Unknown problem closing server socket");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private static void buildTimeSlots() {
        for (int i = 1, j = 0; i < 13; i++, j++) {
            timeSlots[j] = i + "/01/1981";
        }
    }

    private static void buildAppointments() {
        bookedAppointments.put("Matt", "");
        bookedAppointments.put("Mark", "");
        bookedAppointments.put("Luke", "");
        bookedAppointments.put("John", "");
        int i = 1;
        String date = i + "/15/1981";
        for (String key : bookedAppointments.keySet()) {
            bookedAppointments.put(key, date);
            i++;
        }
    }
}

class ClientThread extends Thread {
    protected static Socket clientSocket = null;
    private String request;
    private String[] timeSlots = null;
    private Map<String, String> appointments = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public ClientThread(Socket appServerConnection, String[] timeSlots, Map<String, String> appointments) {
        try {
            this.clientSocket = appServerConnection;
            synchronized (this) {
                this.timeSlots = timeSlots;
                this.appointments = appointments;
            }
            this.start();
        } catch (Exception e) {
            System.err.println("Problem creating resources for thread");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            String choice = in.readLine();
            if (choice.equals("0")) {
                System.out.println("Finding available time slots...");
                String timeSlot = "";
                synchronized (this) {
                    for (int i = 0; i < timeSlots.length; i++) {
                        timeSlot = timeSlot + "\n" + i + ": " + timeSlots[i];
                    }
                    out.println(timeSlot);
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