package battleships;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameViewController {
    @FXML
    private Label statusLabel;

    public void initialize() {
        statusLabel.setText("Trò chơi bắt đầu!");
    }
}
