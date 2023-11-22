package hr.mperhoc.hnefatafl.controller;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.piece.PieceType;
import hr.mperhoc.hnefatafl.util.DialogUtils;
import hr.mperhoc.hnefatafl.util.GUIUtils;
import hr.mperhoc.hnefatafl.util.NetworkUtils;
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

    private boolean hasPlayerConnected = false;

    public void initialize() {
        ipAddressTextField.setText(NetworkUtils.getLocalAddress());
        onlineIpAddressTextField.setText(NetworkUtils.getPublicAddress());
    }

    public void backToMainMenu() {
        GUIUtils.loadScene(backButton, "menu-screen.fxml");
    }

    public void hostGame() {
        Game.startServer();
        PieceType selectedSide = attackerRadioButton.isSelected() ? PieceType.ATTACKER : PieceType.DEFENDER;
        Game.connect(ipAddressTextField.getText(), selectedSide);

        Alert alert = DialogUtils.getHostWaitDialog();
        hasPlayerConnected = false;

        Thread waitForClientThread = new Thread(() -> {
            while (alert.isShowing()) {
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
                // alert.close();
                GUIUtils.loadScene(backButton, "main-screen.fxml");
            } else {
                Game.disconnect();
                Game.stopServer();
            }
        });

        waitForClientThread.start();
    }
}
