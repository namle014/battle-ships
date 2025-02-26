package client;

import common.Network.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RoomListScene {
    private static Stage stage;
    private static NetworkManager network;
    private static ListView<String> roomListView;

    public static void show(Stage primaryStage, NetworkManager net) {
        stage = primaryStage;
        network = net;

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Label label = new Label("Available Rooms:");
        roomListView = new ListView<>();
        roomListView.setPrefHeight(150);

        TextField roomIdField = new TextField();
        roomIdField.setPromptText("Enter Room ID");
        Button joinButton = new Button("Join Room");
        Button backButton = new Button("Back");

        joinButton.setOnAction(e -> {
            String selectedRoom = roomListView.getSelectionModel().getSelectedItem();
            String roomId = selectedRoom != null ? selectedRoom.split(" - ")[0] : roomIdField.getText();
            if (!roomId.isEmpty()) {
                network.joinRoom(roomId);
            }
        });

        backButton.setOnAction(e -> MainMenuScene.show(stage, network));

        root.getChildren().addAll(label, roomListView, roomIdField, joinButton, backButton);
        Scene scene = new Scene(root, 300, 400);
        stage.setScene(scene);
        stage.show();

        network.requestRoomList(); // Yêu cầu ban đầu
    }

    public static void onRoomListReceived(RoomListResponse response) {
        if (roomListView != null) { // Chỉ cập nhật nếu giao diện đang hiển thị
            Platform.runLater(() -> {
                roomListView.getItems().clear();
                for (RoomInfo room : response.rooms) {
                    String roomInfo = room.roomId + " - " + room.players.size() + "/2 players";
                    roomListView.getItems().add(roomInfo);
                }
            });
        }
    }

    public static void onJoinSuccess() {
        if (network != null && network.getCurrentRoom() != null) {
            Platform.runLater(() -> RoomWaitingScene.show(stage, network, network.getCurrentRoomId()));
        }
    }
}