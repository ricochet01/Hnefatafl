package hr.mperhoc.hnefatafl.thread;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.board.GameMove;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class GetLastGameMoveThread extends GameMoveThread implements Runnable {
    private Label lastGameMoveLabel;

    public GetLastGameMoveThread(Label lastGameMoveLabel) {
        this.lastGameMoveLabel = lastGameMoveLabel;
    }

    @Override
    public void run() {
        while (true) {
            GameMove lastMove = getLastGameMove();

            Platform.runLater(() -> {
                String text = "Last game move:\n" + (lastMove != null ? lastMove.toString() : "");
                lastGameMoveLabel.setText(text);
            });

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
