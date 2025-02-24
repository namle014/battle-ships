package battleships;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    @FXML
    private GridPane gridBoard;

    @FXML
    private Button btnStartGame;

    @FXML
    private Button btnSettings;

    @FXML
    public void initialize() {
        ImageView settingsIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/setting.png")));
        settingsIcon.setFitWidth(57); // Kích thước icon
        settingsIcon.setFitHeight(57);
        btnSettings.setGraphic(settingsIcon); // Thêm icon vào button
    }

    @FXML
    private void handleStartGame()  throws IOException {
        System.out.println("Trò chơi bắt đầu!");
//
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameView.fxml"));
//        Parent gameView = loader.load();
//
//        Stage stage = (Stage) btnStartGame.getScene().getWindow(); // Lấy cửa sổ hiện tại
//        stage.setScene(new Scene(gameView));
//
//        stage.show();
    }
}
