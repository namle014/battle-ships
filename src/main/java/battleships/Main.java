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
import java.util.Stack;

public class Main extends Application {
    private static Stack<Scene> sceneHistory = new Stack<>(); // Lưu lịch sử scene

    public static void pushScene(Scene scene) {
        sceneHistory.push(scene);
    }

    public static Scene popScene() {
        return sceneHistory.isEmpty() ? null : sceneHistory.pop();
    }

    private WaitViewController waitViewController; // Lưu controller để gọi hàm

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1300, 750);

        scene.setCursor(new ImageCursor(
                new Image(getClass().getResourceAsStream("/images/cursor.png"))
        ));

        primaryStage.setTitle("Battle Ships");

        primaryStage.setResizable(true);

        primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setCursor(new ImageCursor(
                        new Image(getClass().getResourceAsStream("/images/cursor.png"))
                ));
            }
        });

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
