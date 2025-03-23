package battleships;

import common.DailyQuestsSession;
import common.UserSession;
import database.DatabaseHelper;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static battleships.Main.popScene;
import static battleships.Main.pushScene;

public class SettingController {
    @FXML
    private Button btnBack;

    @FXML
    private Slider sliderMusic;

    @FXML
    private Slider sliderSound;

    @FXML
    private Region region;

    @FXML
    private BorderPane root;

    @FXML
    private PasswordField txtNewPassword, txtCurrentPassword, txtConfirmPassword;

    @FXML
    public void initialize() {
        HBox.setHgrow(region, Priority.ALWAYS);
        region.setPrefHeight(1);
        region.setTranslateY(18);

        updateSliderFill(sliderMusic);
        updateSliderFill(sliderSound);

        root.setOnMouseClicked(event -> {
            if (!(txtNewPassword.isFocused() || txtCurrentPassword.isFocused() || txtConfirmPassword.isFocused())) {
                return;
            }
            root.requestFocus(); // Khi click ra ngoài, các PasswordField mất focus
        });

        updateVolume();

        PauseTransition debounceMusic = new PauseTransition(Duration.millis(500));
        sliderMusic.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSliderFill(sliderMusic);
            debounceMusic.setOnFinished(e -> updateVolumeInDatabase("music_volume", newVal.intValue()));
            debounceMusic.playFromStart(); // Reset lại nếu tiếp tục thay đổi
        });

        // Tạo debounce cho sliderSound
        PauseTransition debounceSound = new PauseTransition(Duration.millis(500));
        sliderSound.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSliderFill(sliderSound);
            debounceSound.setOnFinished(e -> updateVolumeInDatabase("sound_volume", newVal.intValue()));
            debounceSound.playFromStart(); // Reset lại nếu tiếp tục thay đổi
        });
    }

    private void updateVolumeInDatabase(String column, int value) {
        String query = "UPDATE players SET " + column + " = ? WHERE username = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, value);
            String username = UserSession.getInstance().getUsername();
            stmt.setString(2, username);
            stmt.executeUpdate();
            System.out.println("Updated " + column + " to " + value);
            database.DatabaseHelper.updateSession(username);
        } catch (SQLException e) {
            System.err.println("Failed to update " + column + ": " + e.getMessage());
        }
    }

    private void updateSliderFill(Slider slider) {
        Platform.runLater(() -> {
            Region track = (Region) slider.lookup(".track");
            if (track != null) {
                double percentage = (slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin());

                String style = String.format(
                        "-fx-background-color: linear-gradient(to right, #FF9800 %d%%, #CCCCCC %d%%); " +
                                "-fx-background-radius: 10px;",
                        (int) (percentage * 100), (int) (percentage * 100)
                );

                track.setStyle(style);
            } else {
                System.out.println("Không tìm thấy phần tử .track của slider!");
            }
        });
    }

    @FXML
    private void handleBack() {
        Scene previousScene = popScene();
        if (previousScene != null) {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(previousScene);
        }
    }

    @FXML
    public void handleVolumeChange() {
        double volume = sliderMusic.getValue() / 100.0; // Chuyển về giá trị (0.0 - 1.0)
        SoundManager.setVolume(volume);
    }

    @FXML
    private void handleLogout() throws IOException {
        SoundManager.pauseMusic();
        UserSession.logout();
        DailyQuestsSession.logout();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
        Parent modeView = loader.load();

        Stage stage = (Stage) btnBack.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }

    @FXML
    private void handleChangePassword() {
        String currentPassword = txtCurrentPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();
        String newPassword = txtNewPassword.getText().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        UserSession userSession = UserSession.getInstance();
        String hashedPassword = userSession.getPassword();

        if (!BCrypt.checkpw(currentPassword, hashedPassword)) {
            System.out.println("Wrong password!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Confirm password must match new password!");
            return;
        }

        String newHashed = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));

        String query = "UPDATE players SET password = ? WHERE username = ?";

        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newHashed);
            stmt.setString(2, userSession.getUsername());

            int rowsAffected = stmt.executeUpdate(); // Dùng executeUpdate thay vì executeQuery

            if (rowsAffected == 0) {
                System.out.println("Update failed!");
            } else {
                database.DatabaseHelper.updateSession(userSession.getUsername());
                System.out.println("Password updated successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private void updateVolume() {
        UserSession userSession = UserSession.getInstance();

        int musicVolume = userSession.getMusicVolume();
        int soundVolume = userSession.getSoundVolume();

        sliderMusic.setValue(musicVolume);
        sliderSound.setValue(soundVolume);
    }
}
