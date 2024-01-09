package hr.mperhoc.hnefatafl.thread;

import hr.mperhoc.hnefatafl.board.GameMove;

public class SaveGameMoveThread extends GameMoveThread implements Runnable {
    private GameMove gameMove;

    public SaveGameMoveThread(GameMove gameMove) {
        this.gameMove = gameMove;
    }

    @Override
    public void run() {
        saveNewGameMove(gameMove);
    }
}
