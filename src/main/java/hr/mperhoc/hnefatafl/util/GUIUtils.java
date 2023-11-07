package hr.mperhoc.hnefatafl.util;

import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.board.Tile;
import hr.mperhoc.hnefatafl.piece.Piece;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public final class GUIUtils {
    private static final int MOVE_MARKER_RADIUS = 8;

    private GUIUtils() {
    }

    public static GridPane createGridPane() {
        GridPane boardGrid = new GridPane();

        // Creating row and column constraints
        final int numCols = Board.HEIGHT;
        final int numRows = Board.WIDTH;

        for (int i = 0; i < numCols; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / numCols);

            boardGrid.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / numRows);

            boardGrid.getRowConstraints().add(rowConstraints);
        }

        // Making the GridPane occupy the entire AnchorPane
        AnchorPane.setTopAnchor(boardGrid, 0.0);
        AnchorPane.setLeftAnchor(boardGrid, 0.0);
        AnchorPane.setBottomAnchor(boardGrid, 0.0);
        AnchorPane.setRightAnchor(boardGrid, 0.0);

        return boardGrid;
    }

    public static Circle[] generateBoardMoveMarkers() {
        Circle[] moveMarkers = new Circle[Board.WIDTH * Board.HEIGHT];

        for (int i = 0; i < moveMarkers.length; i++) {
            Circle circle = new Circle(MOVE_MARKER_RADIUS);
            circle.setFill(Color.DARKGREEN);

            GridPane.setHalignment(circle, HPos.CENTER);
            circle.setVisible(false);

            circle.setMouseTransparent(true);
            moveMarkers[i] = circle;
        }

        return moveMarkers;
    }

    public static void renderLegalMoves(List<Tile> legalTiles, Circle[] moveMarkers) {
        for (Tile tile : legalTiles) {
            // Not sure why this is inverted...
            moveMarkers[tile.getY() + tile.getX() * Board.WIDTH].setVisible(true);
        }
    }

    public static void hideMoveMarkers(Circle[] moveMarkers) {
        for (Circle circle : moveMarkers) {
            circle.setVisible(false);
        }
    }

    public static void removeImageFromCell(GridPane boardGrid, ImageView img, int x, int y) {
        ObservableList<Node> childrens = boardGrid.getChildren();
        for (Node node : childrens) {
            if (node instanceof ImageView && boardGrid.getRowIndex(node) == y && boardGrid.getColumnIndex(node) == x) {
                boardGrid.getChildren().remove(img);
                break;
            }
        }
    }

    public static void addImageToCell(GridPane boardGrid, ImageView img, int x, int y) {
        GridPane.setRowIndex(img, y);
        GridPane.setColumnIndex(img, x);

        boardGrid.getChildren().add(img);
    }

    public static ImageView[] createPieceImages(Piece[] pieces, GridPane boardGrid) {
        ImageView[] pieceImages = new ImageView[pieces.length];

        for (int i = 0; i < pieces.length; i++) {
            Piece p = pieces[i];

            if (p == null) {
                pieceImages[i] = null;
                continue;
            }

            ImageView img = p.getImageView();
            img.setMouseTransparent(true);

            // Centering the piece inside the tile
            GridPane.setHalignment(img, HPos.CENTER);

            // Setting the piece image  coordinates
            GridPane.setColumnIndex(img, p.getX());
            GridPane.setRowIndex(img, p.getY());

            boardGrid.add(img, p.getX(), p.getY());

            // Adding the image to the images array
            pieceImages[i] = img;
        }

        return pieceImages;
    }
}
