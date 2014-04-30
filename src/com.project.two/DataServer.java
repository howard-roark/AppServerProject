package com.project.two;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * DataServer will listen for connections from ApplicationServer and decide whether to translate string from client into
 * Binary or Hexadecimal.
 *
 * @author Matthew McGuire
 * @version 0.1 27 April 2014
 */
public class DataServer {
    /**
     * Port to listen for connection to DataServer from ApplicationServer
     */
    private static final int DATA_SERVER_PORT = 2233;

    public static void main(String[] args) {
        try (
                ServerSocket dataServerListener = new ServerSocket(DATA_SERVER_PORT);
        ) {
            Socket appServerComm = dataServerListener.accept();
        } catch (IOException ioe) {
            System.err.println("Problem creating data server listening socket");
            ioe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unknown error creating data server listening socket");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private String determineAction(String raw) {
        String result = "NOT TRANSLATED";
        String[] requestByParts = raw.split("^");
        String format = requestByParts[0];
        String[] idAndFormat = format.split("@");
        if (idAndFormat[1].equals("B")) {
            result = rawToBin(requestByParts[1]);
        } else if (requestByParts[0].equals("H")) {
            result = rawToHex(requestByParts[1]);
        }
        return idAndFormat[0] + "@" + result;
    }

    /**
     * Translate raw string into hexadecimal, format to print space
     * after every 2 characters.
     *
     * @param raw the string to be translated
     */
    private static String rawToHex(String raw) {
        String hexString;
        StringBuilder hexQ = new StringBuilder();
        try {
            byte[] bytes = raw.getBytes("UTF-8");
            for (byte b : bytes) {
                for (int i = 0; i < 1; i++) {
                    hexQ.append(String.format("%02x", b & 0xff));
                }
                hexQ.append(' ');
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        hexString = hexQ.toString();
        return hexString;
    }

    /**
     * Translate raw string into binary, format to print space
     * after every 8 characters.
     *
     * @param raw the string to be translated
     */
    private static String rawToBin(String raw) {
        String binString;
        StringBuilder bin = new StringBuilder();
        try {
            byte[] bytes = raw.getBytes("UTF-8");
            bin = new StringBuilder();
            for (byte b : bytes) {
                int val = b;
                for (int i = 0; i < 8; i++) {
                    bin.append((val & 128) == 0 ? 0 : 1);
                    val <<= 1;
                }
                bin.append(' ');
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        binString = bin.toString();
        return binString;
    }
}
