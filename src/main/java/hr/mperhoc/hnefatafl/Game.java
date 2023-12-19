package hr.mperhoc.hnefatafl;

import hr.mperhoc.hnefatafl.jndi.Config;
import hr.mperhoc.hnefatafl.jndi.ConfigKey;
import hr.mperhoc.hnefatafl.network.Client;
import hr.mperhoc.hnefatafl.network.Server;
import hr.mperhoc.hnefatafl.piece.PieceType;

import javafx.application.Application;
import javafx.application.Platform;
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
    private static boolean isHost = false;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("menu-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            Platform.runLater(() -> {
                System.out.println("Exiting...");
                if (isInMultiplayer()) {
                    stopServer();
                    disconnect();

                }
                Platform.exit();
            });
        });

        stage.show();
    }

    public static boolean isInMultiplayer() {
        return client != null || server != null;
    }

    public static void startServer(PieceType selectedSide) {
        server = new Server();
        server.start(selectedSide);
        isHost = true;
    }

    public static boolean isGameHost() {
        return isHost;
    }

    public static void stopServer() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    public static Server getServer() {
        return server;
    }

    public static Client getClient() {
        return client;
    }

    public static String getServerAddress() {
        return client.getServerAddress();
    }

    public static void disconnect() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    public static boolean connect(String ip) {
        client = new Client(ip, Config.readIntConfigValue(ConfigKey.SERVER_PORT));
        return client.connect();
    }

    public static boolean canStartMultiplayerGame() {
        return server.canStartMultiplayerGame();
    }

    public static void main(String[] args) {
        new Thread(Application::launch).start();
    }
}