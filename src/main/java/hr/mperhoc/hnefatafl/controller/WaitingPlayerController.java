package hr.mperhoc.hnefatafl.controller;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.util.GUIUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class WaitingPlayerController {
    @FXML
    private Button cancelButton;
    private boolean hasPlayerConnected, hostCancelled = false;

    public void initialize() {
        Platform.runLater(() -> {
            while (!hasPlayerConnected || !hostCancelled) {
                System.out.println("waiting for player");

                // Check if the second player has joined
                if (Game.canStartMultiplayerGame()) {
                    hasPlayerConnected = true;
                    break;
                }

                // Pause every 100 ms
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (hasPlayerConnected) {
                GUIUtils.loadScene(cancelButton, "main-screen.fxml");
            } else {
                Game.stopServer();
            }
        });
    }

    public void cancelHost() {
        hostCancelled = true;
        GUIUtils.loadScene(cancelButton, "host-screen.fxml");
    }
}
