package hr.mperhoc.hnefatafl.board;

import hr.mperhoc.hnefatafl.piece.Piece;
import hr.mperhoc.hnefatafl.piece.PieceType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class GameMove implements Serializable {
    @Serial
    private static final long serialVersionUID = 1781509182325584054L;
    private Piece pieceMoved;
    private int oldX, oldY;
    private int x, y; // Which coordinates does the piece move to
    private LocalDateTime dateTime; // When was the move played

    public GameMove(Piece pieceMoved, int x, int y, LocalDateTime dateTime) {
        this.pieceMoved = pieceMoved;
        this.x = x;
        this.y = y;
        this.dateTime = dateTime;
    }

    public Piece getPieceMoved() {
        return pieceMoved;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return pieceMoved.toString() + " -> (" + x + "," + y + ")\n" + dateTime;
    }
}
