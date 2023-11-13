package hr.mperhoc.hnefatafl.board;

import hr.mperhoc.hnefatafl.board.state.MoveChecker;
import hr.mperhoc.hnefatafl.piece.Piece;
import hr.mperhoc.hnefatafl.piece.PieceType;
import hr.mperhoc.hnefatafl.util.FileUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {
    @Serial
    private static final long serialVersionUID = 2481334113936040532L;
    public static final int WIDTH = 11;
    public static final int HEIGHT = 11;
    public static final Tile EMPTY_TILE = new Tile(-1, -1, TileType.EMPTY);

    private Tile[] tiles;
    // Storing all the pieces in a list
    private Piece[] pieces;
    private PieceType currentTurn;
    private PieceType winner;

    public Board(String boardFilePath) {
        String board = FileUtils.readTextFileFromResource(boardFilePath);

        initBoard(board);
        generatePieces();
    }

    private void initBoard(String board) {
        tiles = new Tile[WIDTH * HEIGHT];
        // Going through every character in the file
        int tileIndex = 0;

        for (int i = 0; i < board.length(); i++) {
            char c = board.charAt(i);

            if (!Character.isDigit(c)) continue;

            int charValue = c - '0'; // Getting the numeric representation of the character
            TileType tileType = TileType.fromId(charValue);
            tiles[tileIndex] = new Tile(tileIndex % WIDTH, tileIndex / WIDTH, tileType);

            tileIndex++;
        }
    }

    private void generatePieces() {
        pieces = new Piece[WIDTH * HEIGHT];
        currentTurn = PieceType.ATTACKER;
        winner = null;

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = getTile(x, y);

                switch (tile.getType()) {
                    case ATTACK_SPAWN -> pieces[x + y * WIDTH] = new Piece(x, y, PieceType.ATTACKER);
                    case DEFENDER_SPAWN -> pieces[x + y * WIDTH] = new Piece(x, y, PieceType.DEFENDER);
                    case THRONE -> pieces[x + y * WIDTH] = new Piece(x, y, PieceType.KING);
                }
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return EMPTY_TILE;
        return tiles[x + y * WIDTH];
    }

    public Piece[] getPieces() {
        return pieces;
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return null;
        return pieces[x + y * WIDTH];
    }

    public void setPiece(int x, int y, Piece piece) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return;
        pieces[x + y * WIDTH] = piece;
    }

    public List<Tile> getLegalMovePositions(Piece piece) {
        List<Tile> legalTiles = new ArrayList<>();

        /* Horizontal checks */
        MoveChecker.addHorizontalLegalMoves(this, legalTiles, piece, -1); // Left
        MoveChecker.addHorizontalLegalMoves(this, legalTiles, piece, 1); // Right

        /* Vertical checks */
        MoveChecker.addVerticalLegalMoves(this, legalTiles, piece, -1); // Up
        MoveChecker.addVerticalLegalMoves(this, legalTiles, piece, 1); // Down

        return legalTiles;
    }

    public boolean legalMoveClicked(int xTile, int yTile, Piece piece) {
        List<Tile> tiles = getLegalMovePositions(piece);

        for (Tile tile : tiles) {
            if (tile.getX() == xTile && tile.getY() == yTile) return true;
        }

        return false;
    }

    public void movePiece(int xTo, int yTo, Piece piece) {
        pieces[piece.getX() + piece.getY() * WIDTH] = null;

        // Update piece coordinates
        piece.setX(xTo);
        piece.setY(yTo);
        setPiece(xTo, yTo, piece);
    }

    public PieceType getCurrentTurn() {
        return currentTurn;
    }

    public void changeCurrentTurn() {
        currentTurn = currentTurn == PieceType.ATTACKER ?
                PieceType.DEFENDER : PieceType.ATTACKER;
    }

    public List<Piece> checkForCapturedPieces(Piece currentPiece) {
        return MoveChecker.checkForCapturedPieces(currentPiece, this);
    }

    public void removePiece(int x, int y) {
        pieces[x + y * WIDTH] = null;
    }

    public void setWinner(PieceType side) {
        if (side == PieceType.KING) throw new RuntimeException("Invalid winner!");

        this.winner = side;
    }

    public boolean isGameFinished() {
        return winner != null;
    }

    public Piece getKing() {
        for (Piece piece : pieces) {
            if (piece != null) {
                if (piece.getPieceType() == PieceType.KING) return piece;
            }
        }

        return null;
    }

    public void reset() {
        generatePieces();
    }
}
