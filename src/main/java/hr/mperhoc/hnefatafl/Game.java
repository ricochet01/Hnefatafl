package hr.mperhoc.hnefatafl;

import hr.mperhoc.hnefatafl.board.Board;
import hr.mperhoc.hnefatafl.network.Client;
import hr.mperhoc.hnefatafl.network.Server;
import hr.mperhoc.hnefatafl.piece.PieceType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.io.IOException;

public class Game extends Application {
    public static final String TITLE = "Hnefatafl";
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;

    private static Server server;
    private static Client client;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("menu-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        disconnect();
        stopServer();
    }

    public static boolean isInMultiplayer() {
        if (client == null) return false;

        return client.isConnected();
    }

    public static void startServer(PieceType selectedSide) {
        server = new Server();
        server.start(selectedSide);
    }

    public static void stopServer() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    public static boolean connect(String ip) {
        return connect(ip, null);
    }

    public static void disconnect() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    public static boolean connect(String ip, PieceType side) {
        client = new Client(ip, Server.PORT);
        return client.connect();
    }

    public static void sendGameStatePacket(Board board) {
        client.sendGameStatePacket(board);
    }

    public static PieceType getPlayerSide() {
        return client.getPlayerSide();
    }

    public static boolean canStartMultiplayerGame() {
        return server.canStartMultiplayerGame();
    }

    public static void main(String[] args) {
        launch();
    }
}