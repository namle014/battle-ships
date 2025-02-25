package battleships;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import static battleships.Main.popScene;

public class WaitViewController {
    @FXML
    private Button btnBack;

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
