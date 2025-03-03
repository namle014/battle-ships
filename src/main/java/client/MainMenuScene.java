package client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuScene {
    private static Stage stage;
    private static NetworkManager network;

    public static void show(Stage primaryStage, NetworkManager net) {
        stage = primaryStage;
        network = net;

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Button playWithFriend = new Button("Play with Friend");
        Button playOnline = new Button("Play Online");

        playWithFriend.setOnAction(e -> network.createRoom());
        playOnline.setOnAction(e -> RoomListScene.show(stage, network));

        root.getChildren().addAll(playWithFriend, playOnline);
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
    }

    public static void onRoomCreated(String roomId) {
        Platform.runLater(() -> RoomWaitingScene.show(stage, network, roomId));
    }
}