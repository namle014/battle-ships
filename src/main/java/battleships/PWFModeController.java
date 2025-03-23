package battleships;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Optional;
import java.io.IOException;

import static battleships.Main.popScene;
import static battleships.Main.pushScene;

public class PWFModeController {
    @FXML
    private Button btnBack;

    @FXML
    private Button joinButton, cancelButton;

    @FXML
    private BorderPane root;

    @FXML
    private Button btnCreateGame;

    @FXML
    public void initialize() {
        textCode.textProperty().addListener((observable, oldValue, newValue) -> {
            textCode.setText(newValue.replaceAll("[^\\d]", ""));
        });

        root.setOnMouseClicked(event -> {
            if (!textCode.isFocused()) return; // Nếu đã mất focus rồi thì bỏ qua
            textCode.getParent().requestFocus(); // Yêu cầu focus vào phần tử cha
        });
    }

    @FXML
    private TextField textCode;
    @FXML
    private StackPane dialogContainer;

    @FXML
    private void onJoinClicked() {
        String code = textCode.getText();
        if (!code.isEmpty()) {
            System.out.println("Mã phòng nhập vào: " + code);
            dialogContainer.setVisible(false); // Ẩn dialog sau khi nhập mã
        }
    }

    @FXML
    private void onCancelClicked() {
        dialogContainer.setVisible(false); // Ẩn dialog nếu nhấn Cancel
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
        textCode.clear();
        dialogContainer.setVisible(true);
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
