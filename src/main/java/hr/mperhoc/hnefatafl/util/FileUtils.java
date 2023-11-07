package hr.mperhoc.hnefatafl.util;

import hr.mperhoc.hnefatafl.Game;
import hr.mperhoc.hnefatafl.board.Board;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static final String INVALID_FILE_RESULT = "Error";
    public static final String FILENAME = "save.dat";

    private FileUtils() {
    }

    public static String readTextFileFromResource(String path) {
        try (var in = Game.class.getResourceAsStream(path)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return INVALID_FILE_RESULT;
        }
    }

    public static boolean saveGame(Board board) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME));
            oos.writeObject(board);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Board loadGame() {
        Board board = null;

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME));
            board = (Board) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return board;
    }
}
