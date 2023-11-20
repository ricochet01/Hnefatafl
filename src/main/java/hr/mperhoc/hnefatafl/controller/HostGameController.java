package hr.mperhoc.hnefatafl.controller;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.util.GUIUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HostGameController {
    @FXML
    private TextField ipAddressTextField;
    @FXML
    private TextField onlineIpAddressTextField;
    @FXML
    private RadioButton attackerRadioButton;
    @FXML
    private RadioButton defenderRadioButton;
    @FXML
    private Button hostGameButton;
    @FXML
    private Button backButton;

    public void backToMainMenu() {
        GUIUtils.loadScene(backButton, "menu-screen.fxml");
    }
}
