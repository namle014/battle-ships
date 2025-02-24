package battleships;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1300, 750);

        scene.setCursor(new ImageCursor(
                new Image(getClass().getResourceAsStream("/images/cursor.png"))
        ));

        primaryStage.setTitle("Battle Ships");

        // Cho phép thay đổi kích thước để không vô hiệu hóa nút Maximize
        primaryStage.setResizable(true);

        // Ngăn người dùng kéo giãn cửa sổ bằng chuột
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryStage.isMaximized()) primaryStage.setWidth(1300);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryStage.isMaximized()) primaryStage.setHeight(750);
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
