package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.network.packet.ConnectionPacket;
import hr.mperhoc.hnefatafl.network.packet.Packet;
import hr.mperhoc.hnefatafl.network.packet.PacketHeaders;
import hr.mperhoc.hnefatafl.piece.Piece;
import hr.mperhoc.hnefatafl.piece.PieceType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    // The IP address which we're connecting to
    private String ipAddress;
    private int port;
    private Socket socket;

    private boolean listening;
    private Thread thread;
    private boolean connected = false;

    public Client(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public synchronized boolean connect(PieceType side) {
        try {
            socket = new Socket(ipAddress, port);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        sendLoginPacket(side);

        listening = true;
        thread = new Thread(this::listen);
        thread.start();

        return true;
    }

    public synchronized void disconnect() {
        listening = false;

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void listen() {
        while (listening) {
            process();
        }
    }

    private void process() {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            // Accept packets here
            Packet packet = (Packet) ois.readObject();
            handlePacket(packet);
        } catch (IOException | ClassNotFoundException e) {
            // e.printStackTrace();
        }
    }

    private void handlePacket(Packet packet) {
        switch (packet.getHeader()) {
            case PacketHeaders.LOGIN -> {
                connected = true;
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }

    private void sendLoginPacket(PieceType side) {
        send(new ConnectionPacket(side));
    }

    private void send(Packet packet) {
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
