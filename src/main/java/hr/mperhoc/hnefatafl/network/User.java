package hr.mperhoc.hnefatafl.network;

import hr.mperhoc.hnefatafl.piece.PieceType;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return port == user.port && Objects.equals(ip, user.ip) && side == user.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, side);
    }
}
