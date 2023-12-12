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

public class Client {
    public static final int PORT = 9456;

    // The IP address which we're connecting to
    private String ipAddress;
    // The server port
    private int port;
    private Socket socket;

    private boolean listening;
    private Thread thread;
    private boolean connected = false;
    private PieceType playerSide;
    private GameController game;

    public Client(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public synchronized boolean connect() {
        listening = true;
        thread = new Thread(this::listen);
        thread.setDaemon(true);
        thread.start();

        sendLoginPacket();

        return true;
    }

    public synchronized void disconnect() {
        listening = false;
    }

    public void setGameController(GameController game) {
        this.game = game;
    }

    private void listen() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Client listening on port: " + serverSocket.getLocalPort());

            while (listening) {
                Socket clientSocket = serverSocket.accept();
                Platform.runLater(() -> process(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(Socket client) {
        try (ObjectInputStream ois = new ObjectInputStream(client.getInputStream())) {
            Packet packet = (Packet) ois.readObject();
            handlePacket(packet);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(Packet packet) {
        switch (packet.getHeader()) {
            case PacketHeaders.LOGIN -> {
                this.playerSide = ((ConnectionPacket) packet).getPlayerSide();
                connected = true;
                System.out.println("CLIENT has connected");
            }
            case PacketHeaders.GAME_STATE -> {
                GameStatePacket gameStatePacket = (GameStatePacket) packet;
                game.updateGameState(gameStatePacket.getBoard());
            }
            default -> {
                throw new RuntimeException("Invalid packet header!");
            }
        }
    }

    public PieceType getPlayerSide() {
        return playerSide;
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendGameStatePacket(Board board) {
        Packet packet = new GameStatePacket(board);
        send(packet);
    }

    private void sendLoginPacket() {
        System.out.println("Sending login packet");
        // It's null because we don't know the player side
        send(new ConnectionPacket(null));
    }

    private void send(Packet packet) {
        try (Socket socket = new Socket(ipAddress, Server.PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
