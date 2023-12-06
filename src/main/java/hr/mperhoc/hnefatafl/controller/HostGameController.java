package hr.mperhoc.hnefatafl.controller;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.piece.PieceType;
import hr.mperhoc.hnefatafl.util.DialogUtils;
import hr.mperhoc.hnefatafl.util.GUIUtils;
import hr.mperhoc.hnefatafl.util.NetworkUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;


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

    public void initialize() {
        ipAddressTextField.setText(NetworkUtils.getLocalAddress());
        onlineIpAddressTextField.setText(NetworkUtils.getPublicAddress());
    }

    public void backToMainMenu() {
        GUIUtils.loadScene(backButton, "menu-screen.fxml");
    }

    public void hostGame() {
        PieceType selectedSide = attackerRadioButton.isSelected() ? PieceType.ATTACKER : PieceType.DEFENDER;
        Game.startServer(selectedSide);

        GUIUtils.loadScene(hostGameButton, "waiting-player-screen.fxml");
    }
}
