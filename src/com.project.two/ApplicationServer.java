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
 * Application server creates a server socket to listen for client connections and client threads to run the application
 * for each connected client.
 *
 * @author Matthew McGuire
 * @version 1.2 27 April 2014
 */
public class ApplicationServer {

    private static final int PORT = 2222;
    protected static List<String> timeSlots = new ArrayList<>();
    protected static Map<String, String> bookedAppointments = new HashMap<>();
    private static ServerSocket appServer = null;
    private static Socket clientSocket = null;

    /**
     * Main method will create list of available time slots and map of already booked appointments where the key is
     * the customers name and the value is the date of the appointment.
     *
     * @param args
     */
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
            ioe.printStackTrace();
            System.exit(1);
        } finally {
            try {
                System.out.println("Closing clientSocket...");
                clientSocket.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Build the list of available time slots for customers to choose from.
     */
    private static void buildTimeSlots() {
        for (int i = 1, j = 0; i < 13; i++, j++) {
            timeSlots.add(i + "/01/1981");
        }
    }

    /**
     * Build the map for booked appointments for customers to check for their previously booked appointment.
     */
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

/**
 * For each client socket connection a new thread will be made to handle running the application so that
 * the server may continue to listen for additional connections.
 */
class ClientThread extends Thread {

    private Socket clientSocket = null;
    private List<String> timeSlots;
    private Map<String, String> appointments = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String clientName;
    private String fromClient;

    /**
     * Constructor to build client thread for each connected client.
     *
     * @param appServerConnection socket connection for this particular client
     * @param timeSlots           synchronized list of available time slots
     * @param appointments        synchronized map of booked appointments
     */
    public ClientThread(Socket appServerConnection, List<String> timeSlots, Map<String, String> appointments) {
        try {
            this.clientSocket = appServerConnection;
            this.timeSlots = timeSlots;
            this.appointments = appointments;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Close the client connection with customer is done using application leaving the application server running
     * to continue listening for more connections.
     */
    private void closeConnection() {
        out.close();
        try {
            in.close();
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Will print out the available time slots to the customer so that they may choose a date that works for them.
     */
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

                        if ((timeSlots.get(slotChoiceIndex) != null)) {
                            appointments.put(clientName, timeSlots.get(slotChoiceIndex));
                            timeSlots.remove(slotChoiceIndex);
                            break;
                        }
                    } else {
                        out.println("Invalid Entry!");
                    }
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Print the map of booked appointments to the customer so that may confirm that the appointment they booked
     * previously is still in the system.
     */
    private void confirmTimeSlot() {
        System.out.println("Looking up current appointments...");
        out.println("\n" + clientName + "- your appointment is set for: " + appointments.get(clientName) + "\n");
    }

    /**
     * Ability to show all appointments that are in the map, would not be reasonable for real application, just for
     * demonstration.
     */
    private void showAllAppointments() {
        for (String name : appointments.keySet()) {
            out.println("\n" + name + ": " + appointments.get(name) + "\n");
        }
    }

    /**
     * Confirm that the string option passed in by the client is an integer so that it can be used to access the
     * list by index value.
     *
     * @param s option that customer has chosen
     * @return true for integer, false for non-integer
     */
    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Run method to start the thread created for newly connected client.
     */
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
                out.println("\t\t0: See Available Time Slots\n\t\t1: Confirm your chosen time slot\n\t\t2: Show all" +
                        "booked appointments\n\t\t3: Exit\n\n");
                fromClient = in.readLine();
                if (fromClient.equalsIgnoreCase("quit")) {
                    break;
                } else if (fromClient.equals("0")) {
                    showTimeSlots();
                } else if (fromClient.equals("1")) {
                    confirmTimeSlot();
                } else if (fromClient.equals("2")) {
                    showAllAppointments();
                } else if (fromClient.equals("3")) {
                    out.println("Good bye, thank you!");
                    break;
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        closeConnection();
    }
}