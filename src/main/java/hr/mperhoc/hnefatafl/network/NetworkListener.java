package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.controller.GameController;
import hr.mperhoc.hnefatafl.network.packet.Packet;
import hr.mperhoc.hnefatafl.piece.PieceType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class NetworkListener {
    protected PieceType playerSide;
    protected GameController game;
    protected Thread thread;
    protected boolean listening;

    protected final void startThread() {
        listening = true;
        thread = new Thread(this::listen);
        thread.setDaemon(true);
        thread.start();
    }

    protected void process(Socket client) {
        try (ObjectInputStream ois = new ObjectInputStream(client.getInputStream())) {
            Packet packet = (Packet) ois.readObject();
            handlePacket(packet);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void send(Packet packet, String ip, int port) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGameController(GameController game) {
        this.game = game;
    }

    public PieceType getPlayerSide() {
        return playerSide;
    }

    protected abstract void listen();

    public abstract void sendGameStatePacket(Board board);

    public abstract void sendChatPacket();

    protected abstract void handlePacket(Packet packet);

}
