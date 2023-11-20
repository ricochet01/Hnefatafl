package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.board.Board;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    private String ipAddress;
    private int port;
    private Socket socket;

    private boolean listening;
    private Thread thread;

    public Client(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public synchronized boolean connect() {
        try {
            socket = new Socket(ipAddress, port);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        listening = true;

        thread = new Thread(this::listen);
        thread.start();

        return true;
    }

    private void listen() {
        while (listening) {
            new Thread(this::processPacket).start();
        }
    }

    private void processPacket() {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            // currentGameState = (Board) ois.readObject();
            // Accept packets here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
