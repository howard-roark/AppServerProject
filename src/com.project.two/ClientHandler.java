package com.project.two;

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
public class ClientHandler {

    /**
     * Application server to direct traffic to DataServer
     */
    private static ApplicationServer appServer = null;
    /**
     * DataServer to translate string from client to Bin or Hex and return through ApplicationServer
     */
    private static DataServer dataServer = null;

    public static void main(String[] args) {
        appServer = new ApplicationServer();
        dataServer = new DataServer();
    }
}
