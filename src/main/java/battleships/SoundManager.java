package battleships;
import common.UserSession;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayer mediaPlayer;

    private SoundManager() {} // private constructor

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playBackgroundMusic(String filePath) {
        try {
            Media sound = new Media(new File(filePath).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
            mediaPlayer.setVolume(1.0 * UserSession.getInstance().getMusicVolume() / 100);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSound(String filePath) {
        try {
            Media sound = new Media(new File(filePath).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(1.0 * UserSession.getInstance().getSoundVolume() / 100);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }
}


