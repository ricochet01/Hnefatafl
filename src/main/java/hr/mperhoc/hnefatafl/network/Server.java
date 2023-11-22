package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.network.packet.ConnectionPacket;
import hr.mperhoc.hnefatafl.network.packet.Packet;
import hr.mperhoc.hnefatafl.network.packet.PacketHeaders;
import hr.mperhoc.hnefatafl.piece.PieceType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    public static final int PORT = 6502;

    private ServerSocket socket;
    private Thread thread;
    private boolean listening;
    private Board currentGameState;
    private Set<User> connectedUsers;

    public Server() {
        connectedUsers = new HashSet<>();
    }

    public Set<User> getConnectedUsers() {
        return connectedUsers;
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
                String ip = connectedClient.getInetAddress().getHostAddress();
                int port = connectedClient.getPort();

                System.out.println("Client connected from " + ip + ":" + port);

                process(connectedClient);
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
    }

    private void process(Socket connectedClient) {
        String ip = connectedClient.getInetAddress().getHostAddress();
        int port = connectedClient.getPort();

        try (ObjectInputStream ois = new ObjectInputStream(connectedClient.getInputStream())) {
            Packet packet = (Packet) ois.readObject();
            handlePacket(packet, ip, port, connectedClient);
        } catch (IOException | ClassNotFoundException e) {
            // This usually prints an EOFException when a connection closes
            // e.printStackTrace();
        }
    }

    private void handlePacket(Packet packet, String ip, int port, Socket client) {
        switch (packet.getHeader()) {
            case PacketHeaders.LOGIN -> {
                ConnectionPacket connPacket = (ConnectionPacket) packet;
                User user = new User(ip, port, connPacket.getPlayerSide());
                addUser(user, client);
            }
        }
    }

    private void send(Packet packet, Socket client) {
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addUser(User user, Socket client) {
        if (!connectedUsers.isEmpty()) {
            User connected = connectedUsers.iterator().next();
            PieceType secondSide = connected.getSide() == PieceType.ATTACKER
                    ? PieceType.DEFENDER : PieceType.ATTACKER;

            user.setSide(secondSide);
            // Acknowledge the second user's connection and send him his play side
            send(new ConnectionPacket(secondSide), client);
        }

        // The user sides match
        connectedUsers.add(user);

//        System.out.println("added user on server");
    }
}
