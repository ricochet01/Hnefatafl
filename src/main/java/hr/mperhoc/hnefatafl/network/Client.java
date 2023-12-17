package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.controller.GameController;
import hr.mperhoc.hnefatafl.network.packet.*;
import hr.mperhoc.hnefatafl.piece.PieceType;
import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends NetworkListener {
    public static final int CLIENT_PORT = 9456;

    // The IP address which we're connecting to
    private String ipAddress;
    // The server port
    private int port;
    private Socket socket;
    private boolean connected = false;

    public Client(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public synchronized boolean connect() {
        startThread();
        sendLoginPacket();

        return true;
    }

    public synchronized void disconnect() {
        listening = false;
    }

    public void sendLoginPacket() {
        System.out.println("Sending login packet");
        // It's null because we don't know the player side; the server will send it back to us
        send(new ConnectionPacket(null), ipAddress, Server.SERVER_PORT);
    }

    @Override
    public void listen() {
        try (ServerSocket serverSocket = new ServerSocket(CLIENT_PORT)) {
            System.out.println("Client listening on port: " + serverSocket.getLocalPort());

            while (listening) {
                Socket clientSocket = serverSocket.accept();
                Platform.runLater(() -> process(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendGameStatePacket(Board board) {
        Packet packet = new GameStatePacket(board);
        send(packet, ipAddress, Server.SERVER_PORT);
    }

    @Override
    public void sendChatPacket() {
        send(new ChatPacket(), ipAddress, Server.SERVER_PORT);
    }

    @Override
    protected void handlePacket(Packet packet) {
        switch (packet.getHeader()) {
            case PacketHeaders.LOGIN -> {
                this.playerSide = ((ConnectionPacket) packet).getPlayerSide();
                connected = true;
                System.out.println("CLIENT has connected");
                System.out.println("CLIENT is playing as " + playerSide);
            }
            case PacketHeaders.GAME_STATE -> {
                GameStatePacket gameStatePacket = (GameStatePacket) packet;
                game.updateGameState(gameStatePacket.getBoard());
            }
            case PacketHeaders.PING -> {
                System.out.println("PING packet");
            }
            case PacketHeaders.CHAT -> {
                game.updateChatTextArea();
            }
            default -> {
                throw new RuntimeException("Invalid packet header!");
            }
        }
    }
}
