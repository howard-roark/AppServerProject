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
    private String request;
    private List<String> timeSlots;
    private Map<String, String> appointments = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String clientName;

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

    @Override
    public void run() {
        String slotChoice;
        String customerName;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            String choice = in.readLine();
            if (choice.equals("0")) {
                System.out.println("Finding available time slots...");
                String timeSlot = null;
                synchronized (this) {
                    int i = 0;
                    for (String slot : timeSlots) {
                        timeSlot = timeSlot + "\n" + i + ": " + slot;
                        i++;
                    }
                }
                out.println(timeSlot);
                out.println("Please choose the time slot that works best for you: ");
                int slotChoiceIndex = -1;
                while (true) {
                    while ((slotChoice = in.readLine()) != null) {
                        synchronized (this) {
                            slotChoiceIndex = Integer.parseInt(slotChoice);
                            System.out.println("Client chose: " + slotChoice);

                            if ((timeSlots.get(slotChoiceIndex) != null) && (!timeSlots.get(slotChoiceIndex).contains("null"))) {
                                out.print("Please enter your name: ");
                                customerName = in.readLine();
                                appointments.put(customerName, timeSlots.get(slotChoiceIndex));
                                timeSlots.remove(slotChoiceIndex);
                            } else {
                                out.println("Invalid Entry, System shutting down");
                            }
                        }
                    }
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
}