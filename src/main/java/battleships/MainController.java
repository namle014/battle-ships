package battleships;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private void handleStartGame()  throws IOException {
        System.out.println("Trò chơi bắt đầu!");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameView.fxml"));
        Parent gameView = loader.load();

        Stage stage = (Stage) btnStartGame.getScene().getWindow(); // Lấy cửa sổ hiện tại
        stage.setScene(new Scene(gameView));
        stage.show();
    }
}
