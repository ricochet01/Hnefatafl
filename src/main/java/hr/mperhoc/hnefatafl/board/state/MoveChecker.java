package hr.mperhoc.hnefatafl.board.state;

import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.board.Tile;
import hr.mperhoc.hnefatafl.board.TileType;
import hr.mperhoc.hnefatafl.piece.Piece;
import hr.mperhoc.hnefatafl.piece.PieceType;

import java.util.ArrayList;
import java.util.List;

public class MoveChecker {
    private MoveChecker() {
    }

    private static PieceType getEnemyPieceType(PieceType current) {
        return current == PieceType.ATTACKER ? PieceType.DEFENDER : PieceType.ATTACKER;
    }

    public static void addHorizontalLegalMoves(Board board, List<Tile> tiles, Piece piece, int dir) {
        int x = piece.getX() + dir;
        PieceType enemy = getEnemyPieceType(piece.getPieceType());

        while (true) {
            Tile tile = board.getTile(x, piece.getY());
            Piece p = board.getPiece(x, piece.getY());

            // Cannot go over the edge of the board nor go through a piece
            if (tile.getType() == TileType.EMPTY || p != null) break;

            if (piece.getPieceType() == PieceType.KING) {
                tiles.add(tile);
            } else {
                if (!isSandwichedBetweenTwoHostileObjects(board, enemy, x, piece.getY())) {
                    // Only the king can step on a "hostile" tile
                    if (!tile.getType().isHostile() && piece.getPieceType() != PieceType.KING) {
                        tiles.add(tile);
                    }
                }
            }
            x += dir;
        }
    }

    public static void addVerticalLegalMoves(Board board, List<Tile> tiles, Piece piece, int dir) {
        int y = piece.getY() + dir;
        PieceType enemy = getEnemyPieceType(piece.getPieceType());

        while (true) {
            Tile tile = board.getTile(piece.getX(), y);
            Piece p = board.getPiece(piece.getX(), y);

            // Cannot go over the edge of the board nor go through a piece
            if (tile.getType() == TileType.EMPTY || p != null) break;

            // The king can move anywhere; it is impossible for him to enter into a box
            if (piece.getPieceType() == PieceType.KING) {
                tiles.add(tile);
            } else {
                if (!isSandwichedBetweenTwoHostileObjects(board, enemy, piece.getX(), y)) {
                    // Only the king can step on a "hostile" tile
                    if (!tile.getType().isHostile() && piece.getPieceType() != PieceType.KING) {
                        tiles.add(tile);
                    }
                }
            }

            y += dir;
        }
    }

    // If a tile or an enemy piece is hostile, it returns true
    private static boolean isObjectHostile(Board board, PieceType enemyType, int x, int y) {
        Piece piece = board.getPiece(x, y);
        Tile tile = board.getTile(x, y);

        if (piece != null) {
            if (piece.getPieceType() == enemyType) return true;
            else if (piece.getPieceType() == PieceType.KING && enemyType == PieceType.DEFENDER) return true;
        }

        return tile.getType().isHostile();
    }

    // Hostile tiles can either be an enemy piece or the throne/escape tile
    private static boolean isSandwichedBetweenTwoHostileObjects(Board board, PieceType enemyType, int x, int y) {
        if (isObjectHostile(board, enemyType, x - 1, y) &&
                isObjectHostile(board, enemyType, x + 1, y)) {
            return true;
        }

        if (isObjectHostile(board, enemyType, x, y - 1) &&
                isObjectHostile(board, enemyType, x, y + 1)) {
            return true;
        }

        return false;
    }

    private static boolean isCaptured(Board board, Piece piece) {
        if (piece == null) return false;

        // We check for a king capture separately
        if (piece.getPieceType() == PieceType.KING) return false;

        PieceType enemy = getEnemyPieceType(piece.getPieceType());

        return isSandwichedBetweenTwoHostileObjects(board, enemy, piece.getX(), piece.getY());
    }

    public static List<Piece> checkForCapturedPieces(Piece currentPiece, Board board) {
        List<Piece> captured = new ArrayList<>();

        int x = currentPiece.getX();
        int y = currentPiece.getY();
        PieceType enemy = getEnemyPieceType(currentPiece.getPieceType());

        Piece left = board.getPiece(x - 1, y);
        Piece right = board.getPiece(x + 1, y);
        Piece up = board.getPiece(x, y - 1);
        Piece down = board.getPiece(x, y + 1);

        if (isCaptured(board, left)) captured.add(left);
        if (isCaptured(board, right)) captured.add(right);
        if (isCaptured(board, up)) captured.add(up);
        if (isCaptured(board, down)) captured.add(down);

        return captured;
    }

}
