package hr.mperhoc.hnefatafl.controller;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.board.Tile;
import hr.mperhoc.hnefatafl.board.state.VictoryChecker;
import hr.mperhoc.hnefatafl.chat.service.RemoteChatService;
import hr.mperhoc.hnefatafl.network.Client;
import hr.mperhoc.hnefatafl.network.NetworkListener;
import hr.mperhoc.hnefatafl.network.Server;
import hr.mperhoc.hnefatafl.piece.Piece;
import hr.mperhoc.hnefatafl.piece.PieceType;
import hr.mperhoc.hnefatafl.util.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

import java.rmi.RemoteException;
import java.util.List;

public class GameController {
    @FXML
    private AnchorPane boardPane;

    @FXML
    private Button newGameButton;
    @FXML
    private Button loadGameButton;
    @FXML
    private Button saveGameButton;
    @FXML
    private Button howToPlayButton;
    @FXML
    private Button exportDocButton;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private TextField messageTextField;
    @FXML
    private Button sendMessageButton;

    private GridPane boardGrid;

    // Game state
    private Board board;
    private Piece selectedPiece;

    // Legal move markers which remain hidden until the user wants to play a move
    private Circle[] moveMarkers;
    private ImageView[] pieceImages;

    private boolean multiplayer = false;
    private NetworkListener networkListener;
    private RemoteChatService remoteChatService;

    public void initialize() {
        boardGrid = GUIUtils.createGridPane();

        // Adding the board grid to the parent pane
        boardPane.getChildren().add(boardGrid);

        init();
    }

    public void newGame() {
        board.reset();
        regeneratePieceImages();
    }

    public void loadGame() {
        board = FileUtils.loadGame();

        // The game successfully loaded
        if (board != null) {
            regeneratePieceImages();
            DialogUtils.showInformationDialog("Loaded!", "Successfully loaded the game!");
        } else {
            DialogUtils.showInformationDialog("Error!", "Cannot load a game save.");
        }
    }

    public void saveGame() {
        if (FileUtils.saveGame(board)) {
            DialogUtils.showInformationDialog("Saved!", "Successfully saved the game!");
        } else {
            DialogUtils.showInformationDialog("Error!", "An error occurred while saving the game");
        }
    }

    public void showGameTutorial() {
        DialogUtils.showTutorialDialog();
    }

    public void exportDocs() {
        String path = DialogUtils.chooseDocumentationPath();

        if (!path.isEmpty()) {
            DocumentationUtils.generateProjectDocumentation(path);
            DialogUtils.showInformationDialog("Success!",
                    "Successfully exported the documentation");
        }
    }

    private boolean wantsToMoveAPiece() {
        return selectedPiece != null;
    }

    private void init() {
        setupBoard();

        // Must be called after we paint each board tile
        boardGrid.setGridLinesVisible(true);

        pieceImages = GUIUtils.createPieceImages(board.getPieces(), boardGrid);

        multiplayer = Game.isInMultiplayer();
        // Disables the New, Load and Save buttons
        if (multiplayer) {
            disableFileButtons();

            boolean isServer = Game.isGameHost();

            networkListener = isServer ? Game.getServer() : Game.getClient();
            remoteChatService = isServer
                    ? RmiUtils.startRmiChatServer()
                    : RmiUtils.startRmiChatClient(Game.getServerAddress());

            networkListener.setGameController(this);
        } else {
            disableTextChat();
        }
    }

    private void disableFileButtons() {
        newGameButton.setDisable(true);
        loadGameButton.setDisable(true);
        saveGameButton.setDisable(true);
    }

    private void disableTextChat() {
        chatTextArea.setVisible(false);
        messageTextField.setVisible(false);
        sendMessageButton.setVisible(false);
    }

    private void regeneratePieceImages() {
        // Removing old images
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                ImageView img = pieceImages[x + y * Board.WIDTH];

                GUIUtils.removeImageFromCell(boardGrid, img, x, y);
            }
        }

        pieceImages = GUIUtils.createPieceImages(board.getPieces(), boardGrid);
    }

    private void setupBoard() {
        // Loading the board
        board = new Board("board.txt");
        moveMarkers = GUIUtils.generateBoardMoveMarkers();

        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                Tile tile = board.getTile(x, y);

                Pane backgroundTile = new Pane();
                // Setting the correct color for each tile
                backgroundTile.setStyle("-fx-background-color: #" + tile.getType().getColor());

                // Ignore mouse clicks on it
                backgroundTile.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    handleOnTileClick(backgroundTile);
                });

                backgroundTile.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    int xTile = GridPane.getColumnIndex(backgroundTile);
                    int yTile = GridPane.getRowIndex(backgroundTile);

                    boolean shouldChangeHoverEffect = false;

                    // Hover if it's a legal move
                    boolean isLegalMove = moveMarkers[yTile + xTile * Board.WIDTH].isVisible() && newValue;
                    // Or if the tile has the selected piece
                    if (wantsToMoveAPiece())
                        shouldChangeHoverEffect = isLegalMove || (selectedPiece.getX() == xTile && selectedPiece.getY() == yTile && newValue)
                                && !board.isGameFinished();

                    String clr = shouldChangeHoverEffect ? tile.getType().getDarkerColor() : tile.getType().getColor();
                    backgroundTile.setStyle("-fx-background-color: #" + clr);
                });

                GridPane.setHalignment(backgroundTile, HPos.CENTER);
                boardGrid.add(backgroundTile, y, x);
                boardGrid.add(moveMarkers[x + y * Board.WIDTH], y, x);
            }
        }
    }

    public void updateGameState(Board board) {
        this.board = board;
        regeneratePieceImages();

        // Check for a winner here
        if (checkWinner(false) != null) newGameButton.setDisable(false);
    }

    private void deselectPiece(Pane tile, int x, int y) {
        selectedPiece = null;
        tile.setStyle("-fx-background-color: #" + board.getTile(x, y).getType().getColor());
    }

    private PieceType checkWinner(boolean changePlayerTurn) {
        // Check for victories
        if (VictoryChecker.kingIsCaptured(board)) {
            // Victory for the attacker
            removePieces(board.getKing());

            declareWinner(PieceType.ATTACKER);
            return PieceType.ATTACKER;
        } else if (VictoryChecker.kingHasEscaped(board)) {
            // Victory for the defender
            declareWinner(PieceType.DEFENDER);
            return PieceType.DEFENDER;
        }

        PieceType turnPlayedBy = board.getCurrentTurn();
        if (changePlayerTurn) board.changeCurrentTurn();

        // The player who just played a move won the game
        // because the opponent has no legal moves.
        if (!VictoryChecker.hasLegalMoves(board, board.getCurrentTurn())) {
            declareWinner(turnPlayedBy);
            return turnPlayedBy;
        }

        return null;
    }

    private void handleOnTileClick(Pane tile) {
        // Disallow any clicks if the game is finished
        if (board.isGameFinished()) return;
        else if (multiplayer && networkListener.getPlayerSide() != board.getCurrentTurn()) return;

        int xTile = GridPane.getColumnIndex(tile);
        int yTile = GridPane.getRowIndex(tile);

        GUIUtils.hideMoveMarkers(moveMarkers);

        if (selectedPiece != null) {
            // If we select the same piece, we deselect it
            if (selectedPiece.getX() == xTile && selectedPiece.getY() == yTile) {
                deselectPiece(tile, xTile, yTile);
                return;
            }

            // Checking if a real move happened
            if (board.legalMoveClicked(xTile, yTile, selectedPiece)) {
                movePieceImage(selectedPiece.getX(), selectedPiece.getY(), xTile, yTile);

                // Move the piece and check for captures
                board.movePiece(xTile, yTile, selectedPiece);
                List<Piece> captured = board.checkForCapturedPieces(selectedPiece);
                if (!captured.isEmpty()) {
                    removePieces(captured.toArray(new Piece[0]));
                }

                if (checkWinner(true) != null) newGameButton.setDisable(false);

                if (multiplayer) networkListener.sendGameStatePacket(board);

                deselectPiece(tile, xTile, yTile);
            } else {
                // If we click on a tile which is not a legal move, deselect the piece
                deselectPiece(tile, xTile, yTile);
            }
        }

        Piece piece = board.getPiece(xTile, yTile);
        if (piece == null) return;

        // The legal moves get rendered after the game finished, so we check for that first
        if (!board.isGameFinished()) {
            if (piece.getPieceType() == board.getCurrentTurn() ||
                    (piece.getPieceType() == PieceType.KING && board.getCurrentTurn() == PieceType.DEFENDER)) {
                selectedPiece = board.getPiece(xTile, yTile);

                // Automatically darken the tile to trigger the hover effect immediately
                tile.setStyle("-fx-background-color: #" + board.getTile(xTile, yTile).getType().getDarkerColor());
                GUIUtils.renderLegalMoves(board.getLegalMovePositions(selectedPiece), moveMarkers);
            }
        }
    }

    private void declareWinner(PieceType side) {
        board.setWinner(side);
        DialogUtils.showWinnerDialog(side);
    }

    private void movePieceImage(int oldX, int oldY, int newX, int newY) {
        int oldIndex = oldX + oldY * Board.WIDTH;
        int newIndex = newX + newY * Board.WIDTH;

        ImageView img = pieceImages[oldIndex];

        GUIUtils.removeImageFromCell(boardGrid, img, oldX, oldY);
        GUIUtils.addImageToCell(boardGrid, img, newX, newY);

        pieceImages[oldIndex] = null;
        pieceImages[newIndex] = img;
    }

    private void removePieces(Piece... captured) {
        for (Piece p : captured) {
            int x = p.getX();
            int y = p.getY();

            board.removePiece(x, y);
            GUIUtils.removeImageFromCell(boardGrid, pieceImages[x + y * Board.WIDTH], x, y);
        }
    }

    public void updateChatTextArea() {
        try {
            List<String> chatMessages = remoteChatService.getAllMessages();
            chatTextArea.clear();

            for (String msg : chatMessages) {
                chatTextArea.appendText(msg + "\n");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendChatMessage() {
        String message = messageTextField.getText().trim();

        try {
            remoteChatService.sendMessage(networkListener.getPlayerSide().name() + ": " + message);
            messageTextField.clear();

            updateChatTextArea();
            networkListener.sendChatPacket();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}