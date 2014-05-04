package com.project.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Matthew McGuire
 * @version 1.0 27 April 2014
 */
public class ApplicationServer {

    private static final int PORT = 2222;
    protected static List<String> timeSlots = new ArrayList<>();
    protected static Map<String, String> bookedAppointments = new HashMap<>();
    private static ServerSocket appServer = null;
    private static Socket clientSocket = null;

    public static void main(String[] args) {
        buildTimeSlots();
        buildAppointments();
        try {
            appServer = new ServerSocket(PORT);
            System.out.println("Application Server listening...");
            while (true) {
                clientSocket = appServer.accept();
                new ClientThread(clientSocket, timeSlots, bookedAppointments).start();
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
            timeSlots.add(i + "/01/1981");
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

    private Socket clientSocket = null;
    private List<String> timeSlots;
    private Map<String, String> appointments = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String clientName;
    private String fromClient;

    public ClientThread(Socket appServerConnection, List<String> timeSlots, Map<String, String> appointments) {
        try {
            this.clientSocket = appServerConnection;
            this.timeSlots = timeSlots;
            this.appointments = appointments;
        } catch (Exception e) {
            System.err.println("Problem creating resources for thread");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void closeConnection() {
        out.close();
        try {
            in.close();
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void showTimeSlots() {
        while (true) {
            System.out.println("Finding available time slots...");
            String timeSlot = "";
            synchronized (this) {
                int i = 0;
                for (String slot : timeSlots) {
                    timeSlot = timeSlot + "\n" + i + ": " + slot;
                    i++;
                }
            }
            out.println(timeSlot);
            out.println("Please choose the time slot that works best for you: ");
            try {
                int slotChoiceIndex;
                fromClient = in.readLine();

                synchronized (this) {
                    if (isInteger(fromClient)) {
                        slotChoiceIndex = Integer.parseInt(fromClient);
                        System.out.println("Client chose: " + fromClient);

                        if ((timeSlots.get(slotChoiceIndex) != null) && (!timeSlots.get(slotChoiceIndex).contains("null"))) {
                            appointments.put(clientName, timeSlots.get(slotChoiceIndex));
                            timeSlots.remove(slotChoiceIndex);
                            break;
                        }
                    } else {
                        out.println("Invalid Entry!");
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void confirmTimeSlot() {
        System.out.println("Looking up current appointments...");
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public void run() {

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        out.println("Welcome to Dr. Dog's Veterinary Clinic's State of the Art Scheduling App!");
        out.println("To quit at any time type \"quit\".");
        out.println("Please enter your name.");

        try {

            fromClient = in.readLine();
            if (fromClient.equalsIgnoreCase("quit")) {
                closeConnection();
            } else {
                clientName = fromClient;
            }

            while (true) {
                out.println("\tPlease choose from the following options:");
                out.println("\t\t0: See Available Time Slots\n\t\t1: Confirm your chosen time slot");
                fromClient = in.readLine();
                if (fromClient.equalsIgnoreCase("quit")) {
                    break;
                } else if (fromClient.equals("0")) {
                    showTimeSlots();
                } else if (fromClient.equals("1")) {
                    confirmTimeSlot();
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        closeConnection();

    }

}
