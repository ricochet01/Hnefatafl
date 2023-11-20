package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.piece.PieceType;

public class User {
    private final String ip;
    private final int port;
    private PieceType side;

    public User(String ip, int port, PieceType side) {
        this.ip = ip;
        this.port = port;
        this.side = side;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public PieceType getSide() {
        return side;
    }

    public void setSide(PieceType side) {
        this.side = side;
    }
}
