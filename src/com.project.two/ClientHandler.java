package com.project.two;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * <p><ul>ClientHandler will perform the following tasks:
 * <li>Create 3 clients that will connect to an Application Server via TCP Sockets.</li>
 * <li>Prompt the user via a command line interface to enter a string that the user wishes to have translated into</li>
 * binary or hexadecimal.</li>
 * <li>Initiate the Application and Data servers so that they are listening for requests on the appropriate ports.</li>
 * </ul></p>
 *
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class ClientHandler extends Thread {
    /**
     * Port in which the ApplicationServer is listening for connections
     */
    private static final int PORT = 2222;

    public static void main(String[] args) {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            try (
                    Socket clientA = new Socket(localhost, PORT);
                    OutputStreamWriter outClientA = new OutputStreamWriter(clientA.getOutputStream());
                    ObjectInputStream inClientA = new ObjectInputStream(clientA.getInputStream());

                    Socket clientB = new Socket(localhost, PORT);
                    PrintStream outClientB = new PrintStream(clientB.getOutputStream());
                    DataInputStream inClientB = new DataInputStream(clientB.getInputStream());


                    Socket clientC = new Socket(localhost, PORT);
                    PrintStream outClientC = new PrintStream(clientC.getOutputStream());
                    ObjectInputStream inClientC = new ObjectInputStream(clientC.getInputStream());


                    InputStreamReader input = new InputStreamReader(System.in);
                    BufferedReader reader = new BufferedReader(input);
            ) {
                if ((clientA != null && outClientA != null && inClientA != null) ||
                        (clientB != null && inClientB != null) ||
                        (clientC != null && inClientC != null)) {
                    while (true) {
                        try {
                            do {
                                System.out.println("Please enter the string to be converted for ClientA:");
                                String userIn = reader.readLine();
                                System.out.println();
                                System.out.println("Enter \'B\' to encode your String in Binary or \'H\' for Hexadecimal:");
                                String format = reader.readLine();
                                System.out.println();
                                try {
                                    Thread threadClientA = new Thread(format + " ^ " + userIn);
                                    outClientA.write(threadClientA.getName());
                                    String response = inClientA.readUTF();
                                    System.out.println(response);
                                } catch (RuntimeException e) {
                                    System.err.println("Error when creating client thread");
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            } while (reader.readLine() != "9");
                            String responseLine;
                            outClientA.write(inClientA.toString());
                            responseLine = inClientA.toString();
                            while (responseLine != null) {
                                System.out.println(responseLine);
                                outClientA.write(inClientA.toString());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error when creating client sockets:");
                e.printStackTrace();
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Could not get local host");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
