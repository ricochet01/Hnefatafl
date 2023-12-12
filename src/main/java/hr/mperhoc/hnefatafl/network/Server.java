package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.controller.GameController;
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
    private String clientIp = "";
    private GameController game;

    public void start(PieceType playerSide) {
        this.playerSide = playerSide;

        listening = true;
        thread = new Thread(this::listen);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        listening = false;
        clientConnected = false;
    }

    public boolean canStartMultiplayerGame() {
        return clientConnected;
    }

    public PieceType getPlayerSide() {
        return playerSide;
    }

    public void setGameController(GameController game) {
        this.game = game;
    }

    private void listen() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port: " + serverSocket.getLocalPort());

            while (listening) {
                Socket clientSocket = serverSocket.accept();
                // System.out.println("Client connected from port: " + clientSocket.getPort());
                if (!clientIp.isEmpty()) this.clientIp = clientSocket.getInetAddress().getHostAddress();

                Platform.runLater(() -> process(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(Socket connectedClient) {
        try (ObjectOutputStream oos = new ObjectOutputStream(connectedClient.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(connectedClient.getInputStream())) {
            Packet packet = (Packet) ois.readObject();
            handlePacket(packet);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(Packet packet) {
        switch (packet.getHeader()) {
            case PacketHeaders.LOGIN -> {
                System.out.println("LOGIN packet");
                clientConnected = true;

                PieceType opponent = playerSide == PieceType.ATTACKER ? PieceType.DEFENDER : PieceType.ATTACKER;
                send(new ConnectionPacket(opponent), clientIp);
            }
            case PacketHeaders.GAME_STATE -> {
                System.out.println("GAME STATE packet");
                GameStatePacket gameStatePacket = (GameStatePacket) packet;
                game.updateGameState(gameStatePacket.getBoard());
            }
            case PacketHeaders.PING -> {
                System.out.println("PING packet");
            }
            default -> {
                throw new RuntimeException("Invalid packet header!");
            }
        }
    }

    public void sendGameStatePacket(Board board) {
        send(new GameStatePacket(board), clientIp);
    }

    private void send(Packet packet, String ip) {
        try (Socket socket = new Socket(ip, Client.PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
