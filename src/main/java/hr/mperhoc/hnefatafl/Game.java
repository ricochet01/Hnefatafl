package hr.mperhoc.hnefatafl;

import hr.mperhoc.hnefatafl.network.Server;
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

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("menu-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static boolean isHost() {
        return server != null;
    }

    public static void startServer() {
        server = new Server();
        server.start();
    }

    public static void stopServer() {
        server.stop();
        server = null;
    }

    public static void main(String[] args) {
        launch();
    }
}