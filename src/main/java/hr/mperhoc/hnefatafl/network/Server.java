package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.network.packet.ConnectionPacket;
import hr.mperhoc.hnefatafl.network.packet.GameStatePacket;
import hr.mperhoc.hnefatafl.network.packet.Packet;
import hr.mperhoc.hnefatafl.network.packet.PacketHeaders;
import hr.mperhoc.hnefatafl.piece.PieceType;
import javafx.application.Platform;

import java.io.*;
import java.net.*;

public class Server {
    public static final int PORT = 6502;

    private Thread thread;
    private boolean listening;
    private boolean clientConnected;
    private PieceType playerSide;

    public void start(PieceType playerSide) {
        this.playerSide = playerSide;

        listening = true;
//        Platform.runLater(() -> listen());
        thread = new Thread(this::listen);
        thread.start();
    }

    public void stop() {
        listening = false;
        clientConnected = false;
    }

    public boolean canStartMultiplayerGame() {
        return clientConnected;
    }

    private void listen() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port: " + serverSocket.getLocalPort());

            while (listening) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from port: " + clientSocket.getPort());

                Platform.runLater(() -> process(clientSocket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void process(Socket connectedClient) {
        try (ObjectOutputStream oos = new ObjectOutputStream(connectedClient.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(connectedClient.getInputStream())) {
            Packet packet = (Packet) ois.readObject();
            handlePacket(packet, connectedClient);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePacket(Packet packet, Socket client) {
        switch (packet.getHeader()) {
            case PacketHeaders.LOGIN -> {
                System.out.println("LOGIN packet");
                clientConnected = true;

                PieceType opponent = playerSide == PieceType.ATTACKER ? PieceType.DEFENDER : PieceType.ATTACKER;
                send(new ConnectionPacket(opponent), client);
            }
            case PacketHeaders.GAME_STATE -> {
                System.out.println("GAME STATE packet");
                GameStatePacket gameStatePacket = (GameStatePacket) packet;
                send(gameStatePacket, client);
            }
            case PacketHeaders.PING -> {
                System.out.println("PING packet");
            }
            default -> {
                throw new RuntimeException("Invalid packet header!");
            }
        }
    }

    private void send(Packet packet, Socket client) {
        try (Socket socket = new Socket(client.getInetAddress(), Client.PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
