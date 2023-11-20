package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.board.Board;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class Server {
    public static final int PORT = 6502;

    private ServerSocket socket;
    private Thread thread;
    private boolean listening;
    private Board currentGameState;

    private Map<String, String> connectedUsers;

    public Server() {
        connectedUsers = new TreeMap<>();
    }

    public synchronized void start() {
        try {
            this.socket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        listening = true;
        thread = new Thread(this::listen);
        thread.start();
    }

    public synchronized void stop() {
        listening = false;

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void listen() {
        while (listening) {
            try {
                Socket connectedClient = socket.accept();
                System.out.println("Client connected from port: " + connectedClient.getPort());

                new Thread(() -> processClient(connectedClient)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processClient(Socket connectedClient) {
        try (ObjectInputStream ois = new ObjectInputStream(connectedClient.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(connectedClient.getOutputStream())) {
            currentGameState = (Board) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
