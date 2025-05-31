package battleships;

import client.NetworkManager;
import common.Network;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static battleships.Main.pushScene;

public class PlayGameViewController extends Application {
    NetworkManager network;
    public void setNetwork(NetworkManager network) {
        this.network = network;
    }

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
    private boolean isPlayerTurn;
    public void setPlayerTurn(boolean isPlayerTurn) {
        this.isPlayerTurn = isPlayerTurn;
    }
    private GameResult gameResult;
    private GameResult opponentGameResult;
    private int streak;
    private int turn;

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
        Scene scene = new Scene(root, 1500, 750);
        primaryStage.setTitle("Battle Ships");
        primaryStage.setScene(scene);

        primaryStage.show();

    }

    @FXML
    public void initialize() throws IOException {
        System.out.println("Open play game view");
        setupGrid(boardGridPlayer, Color.web("#0066CC"), false);
        setupGrid(boardGridOpponent, Color.web("#FF6600"), true);

        // Thiết lập khung chat
        if (chatArea != null) {
            chatArea.setEditable(false); // Không cho chỉnh sửa trực tiếp
        }

        // Xử lý sự kiện nút Send
        if (sendButton != null && messageField != null) {
            sendButton.setOnAction(event -> sendMessage());
        }
        gameResult = new GameResult(0, 0, 0, 0, 0, 0);
        streak = 0;
        turn = 0;
    }

    private void setupGrid(GridPane grid, Color strokeColor, boolean canClick) {
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

                final int rowIndex = row;
                final int colIndex = col;

                if (canClick) {
                    cell.setOnMouseClicked(event -> {
                        if (isPlayerTurn) {
                            attack(colIndex, rowIndex, cell);
                        }
                    });
                }
                grid.add(cell, col, row);
            }
        }
    }

    private Rectangle getRectangleAt(GridPane grid, int col, int row) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                    if (node instanceof Rectangle) {
                        return (Rectangle) node;
                    }
                }
            }
        }
        return null; // Nếu không tìm thấy
    }

    public void setPlayerShips(List<Ship> playerShips, List<Ship> opponentShips) {
        if (playerName != null) {
            playerName.setText(network.getPlayerInfo().username);
        }
        this.playerShips = playerShips;
        this.opponentShips = opponentShips;
        drawShips(boardGridPlayer, playerShips, Color.web("#0066CC"));

        updateTurnUI();
    }

    private void drawShips(GridPane grid, List<Ship> ships, Color shipColor) {
        for (Ship ship : ships) {
            for (int i = 0; i < ship.getSize(); i++) {
                Rectangle rectangle = new Rectangle(45, 45, shipColor);
                rectangle.setStrokeWidth(1);

                if (ship.isHorizontal()) {
                    grid.add(rectangle, ship.getStartX() + i, ship.getStartY());
                } else {
                    grid.add(rectangle, ship.getStartX(), ship.getStartY() + i);
                }
            }
        }
    }

    private void attack(int col, int row, Rectangle cell) {
        turn += 1;
        boolean success = false;
        for (Ship ship : opponentShips) {
            if (ship.isOccupied(col, row)) {
                success = true;
                ship.Attacked();
            }
        }

        cell.setFill(success ? Color.GREEN : Color.RED);
        cell.setOnMouseClicked(null);
        updateGameResult(success);
        boolean win = gameResult.getHits() == 17;
        if (win) {
            endGame(win);
        }
        network.requestAttack(col, row, success, win, gameResult);
        if (!success) switchTurn();
    }

    public void handleAttacked(int col, int row, boolean success, boolean win, GameResult result) {
        turn += 1;
        this.opponentGameResult = result;
        if (!success) {
            Rectangle rec = getRectangleAt(boardGridPlayer, col, row);
            if (rec != null) {
                rec.setFill(Color.RED);
            }
            switchTurn();
        } else {
            Rectangle rectangle = new Rectangle(45, 45, Color.GREEN);
            rectangle.setStrokeWidth(1);
            boardGridPlayer.add(rectangle, col, row);
        }
        if (win) endGame(!win);
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
            network.sendChat(message);
            messageField.clear(); // Xóa ô nhập sau khi gửi
        }
    }

    public void onChatReceived(Network.ChatMessage chat) {
        Platform.runLater(() -> chatArea.appendText(chat.sender + ": " + chat.message + "\n"));
    }

    public void endGame(boolean win) {
        Platform.runLater(() -> {
            Stage stage = (Stage) playerName.getScene().getWindow();

            String fxmlPath = win ? "/WinView.fxml" : "/LoseView.fxml";
            String title = win ? "Win Screen" : "Lose Screen";

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent tempView = loader.load();

                Scene tempScene = new Scene(tempView);
                pushScene(stage.getScene()); // Lưu scene hiện tại nếu cần quay lại

                stage.setScene(tempScene);
                stage.setTitle(title);
                stage.show();

                // Sau 3 giây, chuyển sang ResultView.fxml
                new Thread(() -> {
                    try {
                        Thread.sleep(3000); // Chờ 3 giây
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Platform.runLater(() -> {
                        try {
                            FXMLLoader resultLoader = new FXMLLoader(getClass().getResource("/ResultView.fxml"));
                            Parent resultView = resultLoader.load();
                            ResultViewController controller = resultLoader.getController();
                            controller.setGameResults(gameResult, opponentGameResult, turn);

                            stage.setScene(new Scene(resultView));
                            stage.setTitle("Game Result");
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateGameResult(boolean success) {
        int count = 0;
        for (Ship ship : opponentShips) {
            if (ship.getAttackCount() == ship.getSize()) count += 1;
        }
        gameResult.setShipsDestroyed(count);
        if (success) {
            gameResult.setHits(gameResult.getHits() + 1);
            streak += 1;
            gameResult.setBestStreak(Math.max(gameResult.getBestStreak(), streak));
        }
        else {
            gameResult.setMisses(gameResult.getMisses() + 1);
            streak = 0;
        }
        int accuracy = gameResult.getHits() * 100 / (gameResult.getHits() + gameResult.getMisses());
        gameResult.setAccuracy(accuracy);
        gameResult.setScore(gameResult.getBestStreak() * 3 + gameResult.getHits() + gameResult.getAccuracy());
    }
}
