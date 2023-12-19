package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.jndi.Config;
import hr.mperhoc.hnefatafl.jndi.ConfigKey;
import hr.mperhoc.hnefatafl.network.packet.*;
import hr.mperhoc.hnefatafl.piece.PieceType;
import javafx.application.Platform;

import java.io.*;
import java.net.*;

public class Server extends NetworkListener {
    private boolean clientConnected;
    private String clientIp = "";

    public void start(PieceType playerSide) {
        this.playerSide = playerSide;

        startThread();
    }

    public void stop() {
        listening = false;
        clientConnected = false;
    }

    public boolean canStartMultiplayerGame() {
        return clientConnected;
    }

    @Override
    public void listen() {
        try (ServerSocket serverSocket = new ServerSocket(Config.readIntConfigValue(ConfigKey.SERVER_PORT))) {
            System.out.println("Server listening on port: " + serverSocket.getLocalPort());

            while (listening) {
                Socket clientSocket = serverSocket.accept();
                // System.out.println("Client connected from port: " + clientSocket.getPort());
                if (clientIp.isEmpty()) this.clientIp = clientSocket.getInetAddress().getHostAddress();
                System.out.println(clientIp);

                Platform.runLater(() -> process(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendGameStatePacket(Board board) {
        send(new GameStatePacket(board), clientIp, Config.readIntConfigValue(ConfigKey.CLIENT_PORT));
    }

    @Override
    public void sendChatPacket() {
        send(new ChatPacket(), clientIp, Config.readIntConfigValue(ConfigKey.CLIENT_PORT));
    }

    @Override
    protected void handlePacket(Packet packet) {
        switch (packet.getHeader()) {
            case PacketHeaders.LOGIN -> {
                System.out.println("LOGIN packet");
                clientConnected = true;

                PieceType opponent = playerSide == PieceType.ATTACKER ? PieceType.DEFENDER : PieceType.ATTACKER;
                send(new ConnectionPacket(opponent), clientIp, Config.readIntConfigValue(ConfigKey.CLIENT_PORT));
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
