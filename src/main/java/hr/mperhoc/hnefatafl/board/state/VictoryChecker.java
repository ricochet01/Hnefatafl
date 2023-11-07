package hr.mperhoc.hnefatafl.board.state;

import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.board.TileType;
import hr.mperhoc.hnefatafl.piece.Piece;
import hr.mperhoc.hnefatafl.piece.PieceType;

public class VictoryChecker {

    // public static
    public static boolean hasLegalMoves(Board board, PieceType side) {
        if (side == PieceType.KING) {
            throw new RuntimeException("Invalid team side!");
        }

        for (Piece piece : board.getPieces()) {
            if (piece != null) {
                PieceType pieceType = piece.getPieceType() == PieceType.ATTACKER ? PieceType.ATTACKER : PieceType.DEFENDER;
                if (pieceType != side) continue;

                // Are there legal moves
                if (!board.getLegalMovePositions(piece).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean checkPieceNextToKing(Piece piece) {
        if (piece == null) return false;

        return piece.getPieceType() == PieceType.ATTACKER;
    }

    public static boolean kingIsCaptured(Board board) {
        Piece king = board.getKing();
        int x = king.getX();
        int y = king.getY();

        Piece left = board.getPiece(x - 1, y);
        Piece right = board.getPiece(x + 1, y);
        Piece up = board.getPiece(x, y - 1);
        Piece down = board.getPiece(x, y + 1);

        if (checkPieceNextToKing(left) && checkPieceNextToKing(right)
                && checkPieceNextToKing(up) && checkPieceNextToKing(down)) {
            return true;
        }

        return false;
    }

    public static boolean kingHasEscaped(Board board) {
        Piece king = board.getKing();

        return board.getTile(king.getX(), king.getY()).getType() == TileType.ESCAPE;
    }
}
