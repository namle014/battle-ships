package battleships;
import common.UserSession;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class SoundManager {
    private static MediaPlayer mediaPlayer;

    public static void playBackgroundMusic(String filePath) {
        try {
            // Load file nhạc
            Media sound = new Media(new File(filePath).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);

            // Lặp lại nhạc vô hạn
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));

            mediaPlayer.setVolume(1.0 * UserSession.getInstance().getMusicVolume() / 100);
            // Chạy nhạc
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playSound(String filePath) {
        try {
            // Load file nhạc
            Media sound = new Media(new File(filePath).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);

            mediaPlayer.setVolume(1.0 * UserSession.getInstance().getSoundVolume() / 100);
            // Chạy nhạc
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public static void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public static void resumeMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }
}

