package battleships;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class InviteDialogController extends Application {

    @FXML
    private Button btnAccept, btnDecline;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/InviteDialog.fxml"));
        AnchorPane root = loader.load();
        primaryStage.setTitle("Game Invitation");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML
    private void initialize() {
        btnAccept.setOnAction(event -> {
            System.out.println("Accepted!");
            closeDialog();
        });

        btnDecline.setOnAction(event -> {
            System.out.println("Declined!");
            closeDialog();
        });
    }

    private void closeDialog() {
        Stage stage = (Stage) btnAccept.getScene().getWindow();
        stage.close();
    }
}
