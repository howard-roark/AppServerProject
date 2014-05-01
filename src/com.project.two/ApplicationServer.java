package com.project.two;

import java.io.IOException;
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

    public static void main(String[] args) {
        buildTimeSlots();
        buildAppointments();
        try (
                ServerSocket appServer = new ServerSocket(PORT);
        ) {
            while (true) {
                try {
                    Socket clientSocket = appServer.accept();
                    new ClientThread(clientSocket, timeSlots, bookedAppointments).start();
                } catch (IOException ioe) {
                    System.err.println("Problem sending response to client thread");
                    ioe.printStackTrace();
                    System.exit(1);
                } catch (Exception e) {
                    System.err.println("Unknown error sending response to client thread");
                    e.printStackTrace();
                    System.exit(1);
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
