    package battleships;

    import client.NetworkManager;
    import common.Network;
    import common.UserSession;
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
    import javafx.scene.input.KeyCode;
    import javafx.scene.input.KeyEvent;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.ColumnConstraints;
    import javafx.scene.layout.GridPane;
    import javafx.scene.layout.RowConstraints;
    import javafx.scene.layout.VBox;
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

        // Thêm VBox containers để điều khiển opacity
        private VBox playerBoardContainer;
        private VBox opponentBoardContainer;

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

            // Lấy reference đến các VBox container
            playerBoardContainer = (VBox) boardGridPlayer.getParent();
            opponentBoardContainer = (VBox) boardGridOpponent.getParent();

            // Thiết lập khung chat
            if (chatArea != null) {
                chatArea.setEditable(false); // Không cho chỉnh sửa trực tiếp
            }

            // Xử lý sự kiện nút Send
            if (sendButton != null && messageField != null) {
                sendButton.setOnAction(event -> sendMessage());
            }
            gameResult = new GameResult(0, 0, 0, 0, 0, 0);
            gameResult.setPlayerName(UserSession.getInstance().getUsername());
            streak = 0;
            turn = 0;
        }

        private void setupGrid(GridPane grid, Color strokeColor, boolean canClick) {
            grid.getChildren().clear();
            grid.getColumnConstraints().clear();
            grid.getRowConstraints().clear();

            // Tạo constraints với kích thước nhỏ hơn
            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPrefWidth(30);
            grid.getColumnConstraints().add(column1);

            for (int i = 0; i < 10; i++) {
                ColumnConstraints column = new ColumnConstraints();
                column.setPrefWidth(30);
                grid.getColumnConstraints().add(column);

                Label colLabel = new Label(String.valueOf(i + 1));
                colLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50; -fx-background-color: #E3F2FD; -fx-background-radius: 4px; -fx-padding: 4px;");
                colLabel.setMinSize(30, 30);
                colLabel.setAlignment(Pos.CENTER);
                GridPane.setHalignment(colLabel, javafx.geometry.HPos.CENTER);
                grid.add(colLabel, i + 1, 0);
            }

            RowConstraints row1 = new RowConstraints();
            row1.setPrefHeight(30);
            grid.getRowConstraints().add(row1);

            for (int i = 0; i < 10; i++) {
                RowConstraints row = new RowConstraints();
                row.setPrefHeight(30);
                grid.getRowConstraints().add(row);

                Label rowLabel = new Label(String.valueOf((char) ('A' + i)));
                rowLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50; -fx-background-color: #E3F2FD; -fx-background-radius: 4px; -fx-padding: 4px;");
                rowLabel.setMinSize(30, 30);
                rowLabel.setAlignment(Pos.CENTER);
                GridPane.setHalignment(rowLabel, javafx.geometry.HPos.CENTER);
                grid.add(rowLabel, 0, i + 1);
            }

            for (int row = 1; row <= 10; row++) {
                for (int col = 1; col <= 10; col++) {
                    Rectangle cell = new Rectangle(30, 30);

                    // Màu sắc đơn giản, rõ ràng
                    if (canClick) {
                        // Bảng đối phương - màu xanh nhạt
                        cell.setFill(Color.web("#E1F5FE"));
                        cell.setStroke(Color.web("#0277BD"));
                    } else {
                        // Bảng của mình - màu xanh lá nhạt
                        cell.setFill(Color.web("#E8F5E8"));
                        cell.setStroke(Color.web("#388E3C"));
                    }

                    cell.setStrokeWidth(1);
                    cell.setArcWidth(4);
                    cell.setArcHeight(4);

                    final int rowIndex = row;
                    final int colIndex = col;

                    if (canClick) {
                        // Hiệu ứng hover đơn giản
                        cell.setOnMouseEntered(e -> {
                            if (isPlayerTurn) {
                                cell.setFill(Color.web("#B3E5FC"));
                                cell.setStroke(Color.web("#0277BD"));
//                                cell.setStrokeWidth(1);
                            }
                        });

                        cell.setOnMouseExited(e -> {
                            if (isPlayerTurn) {
                                cell.setFill(Color.web("#E1F5FE"));
                                cell.setStroke(Color.web("#0277BD"));
//                                cell.setStrokeWidth(1);
                            }
                        });

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
                    Rectangle rectangle = new Rectangle(30, 30);

                    // Màu tàu đơn giản, dễ nhận biết
                    rectangle.setFill(Color.web("#90A4AE")); // Xám xanh đậm
                    rectangle.setStroke(Color.web("#388E3C"));
                    rectangle.setArcWidth(4);
                    rectangle.setArcHeight(4);

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
            SoundManager.getInstance().playSound("src/main/resources/sounds/sound.mp3");
            boolean success = false;
            boolean shipsunk = false;

            for (Ship ship : opponentShips) {
                if (ship.isOccupied(col, row)) {
                    success = true;
                    ship.Attacked();
                    if (ship.getAttackCount() == ship.getSize()) {
                        shipsunk = true;
                    }
                }
            }

            // Màu sắc rõ ràng cho kết quả
            if (success) {
                cell.setFill(Color.web("#81C784")); // Xanh lá đậm cho trúng
            } else {
                cell.setFill(Color.web("#FF5252")); // Đỏ đậm cho trượt
            }

            cell.setStrokeWidth(1);
            cell.setOnMouseClicked(null);
            cell.setOnMouseEntered(null);
            cell.setOnMouseExited(null);

            updateGameResult(success);
            boolean win = gameResult.getHits() == 17;
            if (shipsunk && !win) handleShipSunk();
            if (win) {
                endGame(win);
            }
            if (turn == 1 && success) gameResult.setFirstHit(true);
            network.requestAttack(col, row, success, win, gameResult, shipsunk);
            if (!success) switchTurn();
        }

        public void handleAttacked(int col, int row, boolean success, boolean win, GameResult result, boolean shipsunk) {
            Platform.runLater(() -> {
                turn += 1;
                this.opponentGameResult = result;
                if (!success) {
                    Rectangle rec = getRectangleAt(boardGridPlayer, col, row);
                    if (rec != null) {
                        rec.setFill(Color.web("#FF5252"));
                        rec.setStroke(Color.web("#388E3C"));
                    }
                    switchTurn();
                } else {
                    // Xóa rectangle cũ ở vị trí đó
                    Node old = getRectangleAt(boardGridPlayer, col, row);
                    if (old != null) {
                        boardGridPlayer.getChildren().remove(old);
                    }

                    Rectangle hitRect = new Rectangle(30, 30);
                    hitRect.setFill(Color.web("#FFF176")); // vàng chanh = trúng tàu=
                    hitRect.setStroke(Color.web("#388E3C"));
                    hitRect.setArcWidth(4);
                    hitRect.setArcHeight(4);

                    boardGridPlayer.add(hitRect, col, row);
                }
                if (shipsunk && !win) handleShipSunk();
                if (win) endGame(!win);
            });
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
                // Lượt của mình - màu xanh lá
                playerLabel.setText("🔰 YOUR FLEET - DEFENDING");
                playerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; " +
                        "-fx-background-color: #2E7D32; " +
                        "-fx-padding: 12px; -fx-alignment: center; -fx-background-radius: 8px; " +
                        "-fx-border-color: #4CAF50; -fx-border-width: 2px; -fx-border-radius: 8px;");

                opponentLabel.setText("🎯 YOUR TURN - ATTACK!");
                opponentLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; " +
                        "-fx-background-color: #D32F2F; " +
                        "-fx-padding: 12px; -fx-alignment: center; -fx-background-radius: 8px; " +
                        "-fx-border-color: #F44336; -fx-border-width: 3px; -fx-border-radius: 8px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(211,47,47,0.5), 8, 0.5, 0, 0);");

                // Làm nổi bật bảng đối phương
                if (playerBoardContainer != null) {
                    playerBoardContainer.setOpacity(0.8);
                }
                if (opponentBoardContainer != null) {
                    opponentBoardContainer.setOpacity(1.0);
                    opponentBoardContainer.setStyle("-fx-effect: dropshadow(gaussian, rgba(211,47,47,0.3), 10, 0.4, 0, 0);");
                }

            } else {
                // Lượt đối phương - màu cam
                playerLabel.setText("🔱 UNDER ATTACK!");
                playerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; " +
                        "-fx-background-color: #2E7D32; " +
                        "-fx-padding: 12px; -fx-alignment: center; -fx-background-radius: 8px; " +
                        "-fx-border-color: #4CAF50; -fx-border-width: 2px; -fx-border-radius: 8px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(4,227,48,0.5), 8, 0.5, 0, 0);");

                opponentLabel.setText("🏴‍☠️ ENEMY ATTACKING");
                opponentLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; " +
                        "-fx-background-color: #BF360C; " +
                        "-fx-padding: 12px; -fx-alignment: center; -fx-background-radius: 8px; " +
                        "-fx-border-color: #FF5722; -fx-border-width: 2px; -fx-border-radius: 8px;");

                // Làm nổi bật bảng của mình
                if (playerBoardContainer != null) {
                    playerBoardContainer.setOpacity(1.0);
                    playerBoardContainer.setStyle("-fx-effect: dropshadow(gaussian, rgba(245,124,0,0.3), 10, 0.4, 0, 0);");
                }
                if (opponentBoardContainer != null) {
                    opponentBoardContainer.setOpacity(0.8);
                }
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

                try {
                    //scene 1
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent tempView = loader.load();
                    Scene tempScene = new Scene(tempView);

                    //scene 2
                    FXMLLoader resultLoader = new FXMLLoader(getClass().getResource("/ResultView.fxml"));
                    Parent resultView = resultLoader.load();
                    ResultViewController controller = resultLoader.getController();
                    controller.setNetwork(network);
                    controller.setGameResults(gameResult, opponentGameResult, turn);

                    stage.setScene(tempScene);
                    stage.setTitle("Game Result");
                    stage.show();

                    // Sau 3 giây, chuyển sang ResultView.fxml
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000); // Chờ 3 giây
                            Platform.runLater(() -> {
                                stage.setScene(new Scene(resultView));
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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

        private void handleShipSunk() {
            Platform.runLater(() -> {
                Stage stage = (Stage) playerName.getScene().getWindow();

                Main.pushScene(playerName.getScene());
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShipSunkView.fxml"));
                    Parent shipSunkView = loader.load();
                    Scene ssScene = new Scene(shipSunkView);

                    stage.setScene(ssScene);
                    stage.show();

                    // Sau 3 giây, chuyển sang ResultView.fxml
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000); // Chờ 3 giây
                            Platform.runLater(() -> {
                                stage.setScene(Main.popScene());
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        @FXML
        private void handleEnterPress(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                sendButton.fire(); // Kích hoạt nút Send
            }
        }
    }