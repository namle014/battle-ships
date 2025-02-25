package battleships;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

import static battleships.Main.popScene;
import static battleships.Main.pushScene;

public class PWFModeController {
    @FXML
    private Button btnBack;

    @FXML
    private Button btnCreateGame;

    @FXML
    void initialize() {

    }

    @FXML
    private void handleStartGame()   throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/WaitView.fxml"));
        Parent modeView = loader.load();

        Stage stage = (Stage) btnCreateGame.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }

    @FXML
    private void handleJoinGame() {

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
    private void handleNext() {

    }
}
