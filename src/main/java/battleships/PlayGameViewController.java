package battleships;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.List;

public class PlayGameViewController extends Application {

    @FXML
    private GridPane boardGridPlayer;

    @FXML
    private GridPane boardGridOpponent;

    @FXML
    private Label playerLabel;

    @FXML
    private Label opponentLabel;

    @FXML
    private Label playerName;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    private List<Ship> playerShips = new ArrayList<>();
    private List<Ship> opponentShips = new ArrayList<>();
    private boolean isPlayerTurn = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxmlPath = "/PlayGameView.fxml";
        java.net.URL location = getClass().getResource(fxmlPath);
        if (location == null) {
            System.out.println("Không tìm thấy file FXML: " + fxmlPath);
            return;
        }

        FXMLLoader loader = new FXMLLoader(location);
        BorderPane root = loader.load();
        PlayGameViewController controller = loader.getController();
        Scene scene = new Scene(root, 1500, 750);
        primaryStage.setTitle("Battle Ships");
        primaryStage.setScene(scene);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                controller.switchTurn();
            }
        });

        primaryStage.show();
    }

    @FXML
    public void initialize() {
        setupGrid(boardGridPlayer, Color.web("#0066CC"));
        setupGrid(boardGridOpponent, Color.web("#FF6600"));

        createSampleShips();
        drawShips(boardGridPlayer, playerShips, Color.web("#0066CC"));
        drawShips(boardGridOpponent, opponentShips, Color.web("#FF6600"));

        if (playerName != null) {
            playerName.setText("Player 1");
        }

        // Thiết lập khung chat
        if (chatArea != null) {
            chatArea.setEditable(false); // Không cho chỉnh sửa trực tiếp
        }

        // Xử lý sự kiện nút Send
        if (sendButton != null && messageField != null) {
            sendButton.setOnAction(event -> sendMessage());
        }

        updateTurnUI();
    }

    private void setupGrid(GridPane grid, Color strokeColor) {
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPrefWidth(45);
        grid.getColumnConstraints().add(column1);

        for (int i = 0; i < 10; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(45);
            grid.getColumnConstraints().add(column);

            Label colLabel = new Label(String.valueOf(i + 1));
            colLabel.setStyle("-fx-font-weight: bold;");
            colLabel.setMinSize(45, 45);
            colLabel.setAlignment(Pos.BOTTOM_CENTER);
            GridPane.setHalignment(colLabel, javafx.geometry.HPos.CENTER);
            grid.add(colLabel, i + 1, 0);
        }

        RowConstraints row1 = new RowConstraints();
        row1.setPrefHeight(45);
        grid.getRowConstraints().add(row1);

        for (int i = 0; i < 10; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(45);
            grid.getRowConstraints().add(row);

            Label rowLabel = new Label(String.valueOf((char) ('A' + i)));
            rowLabel.setStyle("-fx-font-weight: bold;");
            rowLabel.setMinSize(45, 45);
            rowLabel.setPadding(new Insets(0, 0, 0, 25));
            rowLabel.setAlignment(Pos.CENTER_LEFT);
            GridPane.setHalignment(rowLabel, javafx.geometry.HPos.LEFT);
            grid.add(rowLabel, 0, i + 1);
        }

        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 10; col++) {
                Rectangle cell = new Rectangle(45, 45);
                cell.setFill(Color.color(1.0, 1.0, 1.0, 0.5));
                cell.setStroke(strokeColor);
                cell.setStrokeWidth(1);
                grid.add(cell, col, row);
            }
        }
    }

    private void createSampleShips() {
        playerShips.add(new Ship(1, 1, 5, true));
        playerShips.add(new Ship(3, 3, 4, false));
        playerShips.add(new Ship(5, 5, 3, true));

        opponentShips.add(new Ship(2, 2, 5, false));
        opponentShips.add(new Ship(4, 4, 3, true));
        opponentShips.add(new Ship(6, 6, 2, false));
    }

    private void drawShips(GridPane grid, List<Ship> ships, Color shipColor) {
        for (Ship ship : ships) {
            for (int i = 0; i < ship.getSize(); i++) {
                Rectangle rectangle = new Rectangle(45, 45, shipColor);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(1);

                if (ship.isHorizontal()) {
                    grid.add(rectangle, ship.getStartX() + i, ship.getStartY());
                } else {
                    grid.add(rectangle, ship.getStartX(), ship.getStartY() + i);
                }
            }
        }
    }

    private void switchTurn() {
        isPlayerTurn = !isPlayerTurn;
        updateTurnUI();
    }

    private void updateTurnUI() {
        if (boardGridPlayer == null || boardGridOpponent == null) {
            System.out.println("boardGridPlayer hoặc boardGridOpponent là null trong updateTurnUI!");
            return;
        }
        if (playerLabel == null || opponentLabel == null) {
            System.out.println("playerLabel hoặc opponentLabel là null trong updateTurnUI!");
            return;
        }

        if (isPlayerTurn) {
            boardGridPlayer.setScaleX(0.5);
            boardGridPlayer.setScaleY(0.5);
            boardGridOpponent.setScaleX(1.0);
            boardGridOpponent.setScaleY(1.0);

            playerLabel.setText("YOUR FLEET");
            playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #0066CC; -fx-padding: 10px; -fx-alignment: center; -fx-pref-width: 200px;");
            opponentLabel.setText("YOUR TURN");
            opponentLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #0066CC; -fx-padding: 10px; -fx-alignment: center; -fx-pref-width: 400px;");
        } else {
            boardGridPlayer.setScaleX(1.0);
            boardGridPlayer.setScaleY(1.0);
            boardGridOpponent.setScaleX(0.5);
            boardGridOpponent.setScaleY(0.5);

            playerLabel.setText("ENEMY'S TURN");
            playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #FF6600; -fx-padding: 10px; -fx-alignment: center; -fx-pref-width: 400px;");
            opponentLabel.setText("ENEMY'S FLEET");
            opponentLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #FF6600; -fx-padding: 10px; -fx-alignment: center; -fx-pref-width: 200px;");
        }
    }

    private void sendMessage() {
        if (messageField == null || chatArea == null) {
            System.out.println("messageField hoặc chatArea là null!");
            return;
        }

        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            chatArea.appendText(playerName.getText() + ": " + message + "\n");
            messageField.clear(); // Xóa ô nhập sau khi gửi
        }
    }
}
