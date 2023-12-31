package hr.mperhoc.hnefatafl.controller;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.util.GUIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class JoinScreenController {
    @FXML
    private TextField ipAddressTextField;
    @FXML
    private Button backButton;
    @FXML
    private Button connectButton;

    public void backToMainMenu() {
        GUIUtils.loadScene(backButton, "menu-screen.fxml");
    }

    public void joinGame() {
        // We have successfully joined a game
        if (Game.connect(ipAddressTextField.getText())) {
            // Switch to the game board
            GUIUtils.loadScene(connectButton, "main-screen.fxml");
        }
    }
}
