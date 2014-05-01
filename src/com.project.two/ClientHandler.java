package com.project.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class ClientHandler {
    private static String choice = "";
    /**
     * Port in which the ApplicationServer is listening for connections
     */
    private static final int PORT = 2222;

    public static void main(String[] args) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.println("Welcome to Dr. Dog's Veterinary Clinic State of the Art Scheduling App!");
            System.out.println("\tPlease choose from the following options:");
            System.out.println("\t\t0: See Available Time Slots\n\t\t1: Confirm your chosen time slot\n\t\t2: Exit");
            choice = reader.readLine();
            while ((!choice.equals("0")) && (!choice.equals("1")) && (!choice.equals("2"))) {
                System.out.println("Please choose a valid option: 0, 1 or 2");
                choice = reader.readLine();
            }
            if (choice.equals("0")) {
                //TODO Display available slots in array,need to enter name and choose a time
            } else if (choice.equals("1")) {
                //TODO Look up appointment in map based on client Name, will need to prompt for a name
            } else {
                System.out.println("Thank you, come again!");
                System.exit(0);
            }
        } catch (IOException ioe) {
            System.err.println("Problem creating BufferedReader for reading input from command line");
            ioe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unknown error creating request");
            e.printStackTrace();
            System.exit(1);
        }

        try (
                Socket socket = new Socket("localhost", PORT);
                PrintStream outClient = new PrintStream(socket.getOutputStream());
                BufferedReader inClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            outClient.print(choice);
            String response = null;
            while (response == null) {
                response = inClient.readLine();
                if (choice.equals("0")) {
                    while (response != null) {
                        System.out.println(response);
                    }
                } else if (choice.equals("1") && (response.equals("Please enter your name: ") ||
                        response.equals("Invalid Entry.  Please re-Type your Name: "))) {
                    if (response.equals("Invalid Entry.  Please re-Type your Name: ")) {

                    }
                }
                response = null;
            }
        } catch (UnknownHostException ue) {
            System.err.println("Problem connecting to localhost, check if Apache is running");
            ue.printStackTrace();
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("Problem connecting client socket with Application Server");
            ioe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unknown error creating client sockets");
            e.printStackTrace();
            System.exit(1);
        }

    }
}