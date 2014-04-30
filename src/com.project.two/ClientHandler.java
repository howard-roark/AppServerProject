package com.project.two;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class ClientHandler {
    /**
     * Port in which the ApplicationServer is listening for connections
     */
    private static final int APPLICATION_SERVER_PORT = 2222;
    private static String clientRequest = "";
    private static int clientId = 0;


    public static void main(String[] args) {

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ) {
            while (!reader.readLine().equals("EXIT")) {
                System.out.println("Client Number -  : Please Enter the string you would like " +
                        "translated.");
                String clientString = reader.readLine();
                System.out.println("\nPlease enter \'B\' for a Binary translation of \'H\' for a Hexadecimal " +
                        "translation.");
                String format = reader.readLine();
                while ((!format.equals("B")) && (!format.equals("H"))) {
                    System.out.println("Please enter \'B\' or \'H\'");
                    format = reader.readLine();
                }
                clientRequest = clientId + "@" + format + "^" + clientString;
                clientId++;
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
                Socket socket = new Socket(InetAddress.getLocalHost(), APPLICATION_SERVER_PORT);
                PrintWriter outClient = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            outClient.println(clientRequest);
            String response = "";
            while (response.equals("")) {
                response = inClient.readLine();
                System.out.println(response);
                response = "";
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