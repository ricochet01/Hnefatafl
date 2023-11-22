package hr.mperhoc.hnefatafl;

import hr.mperhoc.hnefatafl.network.Client;
import hr.mperhoc.hnefatafl.network.Server;
import hr.mperhoc.hnefatafl.piece.PieceType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    public static boolean isHost() {
        return server != null;
    }

    public static boolean isInMultiplayer() {
        if (client == null) return false;

        return client.isConnected();
    }

    public static void startServer() {
        server = new Server();
        server.start();
    }

    public static void stopServer() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    public static void connect(String ip) {
        connect(ip, null);
    }

    public static void disconnect() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    public static void connect(String ip, PieceType side) {
        client = new Client(ip, Server.PORT);
        client.connect(side);
    }

    public static boolean canStartMultiplayerGame() {
        return server.getConnectedUsers().size() > 1;
    }

    public static void main(String[] args) {
        launch();
    }
}