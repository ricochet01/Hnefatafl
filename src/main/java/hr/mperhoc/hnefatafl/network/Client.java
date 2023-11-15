package hr.mperhoc.hnefatafl.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    private String ipAddress;
    private int port;
    private Socket socket;

    public Client(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public synchronized boolean connect() {
        try {
            socket = new Socket(ipAddress, port);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
