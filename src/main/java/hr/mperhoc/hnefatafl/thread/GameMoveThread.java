package hr.mperhoc.hnefatafl.thread;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.board.GameMove;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class GameMoveThread {
    private static final String FILE_NAME = "files/moves.dat";
    private static boolean fileAccessInProgress = false;

    public synchronized GameMove getLastGameMove() {
        while (fileAccessInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // We're now starting file I/O
        fileAccessInProgress = true;
        List<GameMove> moves = getAllGameMoves();
        GameMove last = !moves.isEmpty() ? moves.getLast() : null;
        fileAccessInProgress = false;

        // Notify all other threads before returning
        notifyAll();

        return last;
    }

    public synchronized void saveNewGameMove(GameMove gameMove) {
        // If another thread is accessing a file, wait until it's done
        while (fileAccessInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // We're now starting file I/O
        fileAccessInProgress = true;

        // Read all previous moves and write the latest one
        List<GameMove> savedMoves = getAllGameMoves();
        savedMoves.add(gameMove);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FILE_NAME))) {
            oos.writeObject(savedMoves);
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileAccessInProgress = false;
        notifyAll(); // Notify each thread that they may access the file
    }

    @SuppressWarnings("unchecked")
    private synchronized List<GameMove> getAllGameMoves() {
        List<GameMove> moves = new ArrayList<>();

        // Only add moves if the file exists
        if (Files.exists(Path.of(FILE_NAME))) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(FILE_NAME))) {
                moves.addAll((List<GameMove>) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return moves;
    }
}
