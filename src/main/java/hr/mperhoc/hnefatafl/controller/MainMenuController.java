package hr.mperhoc.hnefatafl.controller;

import hr.mperhoc.hnefatafl.util.GUIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainMenuController {
    @FXML
    private Button playOfflineButton;
    @FXML
    private Button hostGameButton;
    @FXML
    private Button joinGameButton;
    @FXML
    private Button quitGameButton;

    // Changing the stage to the single player version
    public void playOffline() {
        // Get the primary stage
        GUIUtils.loadScene(playOfflineButton, "main-screen.fxml");
    }

    public void hostGame() {
        GUIUtils.loadScene(playOfflineButton, "host-screen.fxml");
    }

    public void joinGame() {

    }

    public void quitGame() {
        Stage stage = (Stage) quitGameButton.getScene().getWindow();
        stage.close();
    }
}
