package hr.mperhoc.hnefatafl.board;

import java.io.Serial;
import java.io.Serializable;

public class Tile implements Serializable {
    @Serial
    private static final long serialVersionUID = 3493627390025076462L;
    private final int x, y;
    private TileType type;

    public Tile(int x, int y, TileType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TileType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", type=" + type +
                '}';
    }
}
