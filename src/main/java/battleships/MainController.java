package battleships;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.io.IOException;

import static battleships.Main.pushScene;

public class MainController {

    @FXML
    private GridPane gridBoard;

    @FXML
    private Button btnPlaySingle;

    @FXML
    private Button btnPlayOnline;

    @FXML
    private Button btnPlayWithFriends;

    @FXML
    private Button btnSettings;

    @FXML
    private HBox containerProgress1;

    @FXML
    public void initialize() {
        ImageView settingsIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/setting.png")));
        settingsIcon.setFitWidth(57); // Kích thước icon
        settingsIcon.setFitHeight(57);
        containerProgress1.setMaxWidth(Region.USE_PREF_SIZE);
        containerProgress1.setMaxHeight(Region.USE_PREF_SIZE);
        btnSettings.setGraphic(settingsIcon); // Thêm icon vào button
    }

    @FXML
    private void handleStartGame()  throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PWFModeView.fxml"));
        Parent modeView = loader.load();

        Stage stage = (Stage) btnPlaySingle.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;

    public void updateProgress(int current, int total) {
        double progress = (double) current / total;
        progressBar.setProgress(progress);
        progressLabel.setText(current + "/" + total);
    }
}
