package client;

import common.Network.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RoomWaitingScene {
    private static Stage stage;
    private static NetworkManager network;
    private static String roomId;

    public static void show(Stage primaryStage, NetworkManager net, String id) {
        stage = primaryStage;
        network = net;
        roomId = id;

        updateUI(network.getCurrentRoom());
    }

    public static void onRoomUpdate(RoomUpdate update) {
        Platform.runLater(() -> {
            if (stage != null) { // Kiểm tra stage trước khi cập nhật
                updateUI(update);
            }
        });
    }

    private static void updateUI(RoomUpdate update) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Label roomLabel = new Label("Room ID: " + roomId);
        Label playersLabel = new Label("Players:");
        if (update != null) {
            for (PlayerInfo p : update.players) {
                playersLabel.setText(playersLabel.getText() + "\n- " + p.username);
            }
        }

        Button playButton = new Button("Play");
        playButton.setDisable(update == null || !update.canStart);
        playButton.setOnAction(e -> GamePlayScene.show(stage, network));

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            network.leaveRoom();
            MainMenuScene.show(stage, network);
        });

        root.getChildren().addAll(roomLabel, playersLabel, playButton, exitButton);
        Scene scene = new Scene(root, 300, 300);
        stage.setScene(scene); // stage đã được gán trong show(), nên không null tại đây
    }
}