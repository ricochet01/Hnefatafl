package hr.mperhoc.hnefatafl.util;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.piece.PieceType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;

import java.io.File;

public class DialogUtils {

    private DialogUtils() {
    }

    public static void showInformationDialog(String title, String content) {
        showInformationDialog(title, content, "");
    }

    public static void showInformationDialog(String title, String content, String headerText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(headerText.isEmpty() ? "Message" : headerText);
        alert.showAndWait();
    }

    public static void showWinnerDialog(PieceType side) {
        showInformationDialog("Winner", String.format("The %s has won the game!", side.name().toLowerCase()));
    }

    public static String chooseDocumentationPath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file path");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("HTML files", "*.html"));

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("docs.html");

        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }

        return "";
    }

    public static void showTutorialDialog() {
        final String tutorialText = "Move the pieces horizontally or vertically.\n" +
                "Capture pieces by \"sandwiching\" it with 2 of your pieces.\n" +
                "If a player has no legal moves, he loses the game.\n" +
                "Goal of the attackers: capture the enemy king by" +
                "\nsurrounding it with 4 of your pieces on all 4 sides.\n" +
                "Goal of the defenders: get the king on one of the 4 green corner spaces.";

        showInformationDialog("Tutorial", tutorialText, "How to play " + Game.TITLE);
    }
}
