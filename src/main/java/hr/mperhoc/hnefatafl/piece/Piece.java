package hr.mperhoc.hnefatafl.piece;

import hr.mperhoc.hnefatafl.util.ImageUtils;

import javafx.scene.image.ImageView;

import java.io.Serial;
import java.io.Serializable;

public class Piece implements Serializable {
    @Serial
    private static final long serialVersionUID = 7920086227274259242L;
    private int x, y;
    private final PieceType pieceType;

    public Piece(int x, int y, PieceType pieceType) {
        this.x = x;
        this.y = y;
        this.pieceType = pieceType;
    }

    public ImageView getImageView() {
        String path = String.format("gfx/%s.png", pieceType.name().toLowerCase());
        return new ImageView(ImageUtils.loadImageFromResourceFile(path));
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public PieceType getPieceType() {
        return pieceType;
    }
}
