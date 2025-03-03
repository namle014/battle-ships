package client;

import common.Network.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GamePlayScene {
    private static Stage stage;
    private static NetworkManager network;
    private static TextArea chatArea;

    public static void show(Stage primaryStage, NetworkManager net) {
        stage = primaryStage;
        network = net;

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        chatArea = new TextArea();
        chatArea.setEditable(false);
        TextField messageField = new TextField();
        Button sendButton = new Button("Send");

        sendButton.setOnAction(e -> {
            network.sendChat(messageField.getText());
            messageField.clear();
        });

        root.getChildren().addAll(chatArea, messageField, sendButton);
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
    }

    public static void onChatReceived(ChatMessage chat) {
        Platform.runLater(() -> chatArea.appendText(chat.sender + ": " + chat.message + "\n"));
    }
}