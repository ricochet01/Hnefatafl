package hr.mperhoc.hnefatafl.util;

import hr.mperhoc.hnefatafl.Game;
import javafx.scene.image.Image;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ImageUtils {
    private ImageUtils() {
    }

    public static Image loadImageFromResourceFile(String path) {
        try (var in = Game.class.getResourceAsStream(path)) {
            return new Image(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

