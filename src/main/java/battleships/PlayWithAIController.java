package battleships;

import common.UserSession;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static battleships.Main.popScene;

public class PlayWithAIController extends Application {

    @FXML private GridPane boardGridPlayer;
    @FXML private GridPane boardGridOpponent;
    @FXML private Label playerLabel;
    @FXML private Label opponentLabel;
    @FXML private Label playerName;
    @FXML
    private StackPane dialogContainer;

    private boolean aiPaused = false;
    private int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

    private VBox playerBoardContainer;
    private VBox opponentBoardContainer;

    private List<Ship> playerShips;
    private List<Ship> opponentShips;
    private List<int[]> availableTargets = new ArrayList<>();
    private boolean isPlayerTurn = true;
    private Set<String> visited = new HashSet<>();

    private GameResult playerResult;
    private GameResult AIResult;
    private int turn = 0;
    private int streak = 0;

    private Random random = new Random();

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlayGameView.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Scene scene = new Scene(root, 1500, 750);
        primaryStage.setTitle("Battle Ships (vs AI)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void onCancelClicked() {
        dialogContainer.setVisible(false);
        dialogContainer.setManaged(false);
    }

    public void exitGame() {
        int total = UserSession.getInstance().getTotalPlays() + 1;
        int hits = UserSession.getInstance().getTotalHits() + playerResult.getHits();
        int shots = UserSession.getInstance().getTotalShots() + playerResult.getHits() + playerResult.getMisses();

        String sql = "UPDATE players SET total_plays = ?, total_hits = ?, total_shots = ?" +
                " WHERE id = CAST(? AS UUID)";

        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, total);
            stmt.setInt(2, hits);
            stmt.setInt(3, shots);
            stmt.setObject(4, UUID.fromString(UserSession.getInstance().getUserId()));

            stmt.executeUpdate();

            System.out.println("Successfully end game");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAgreeClicked() throws IOException {
        exitGame();
        Scene previousScene = popScene();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Parent modeView = loader.load();

        // Gọi lại update khi cửa sổ mở lại
        MainController controller = loader.getController();
        controller.updateInfoPersonal();
        controller.updateDailyQuests();

        if (previousScene != null) {
            Stage stage = (Stage) playerName.getScene().getWindow();
            stage.setScene(previousScene);
        }
    }
    @FXML
    private void handleBack() throws IOException {
        dialogContainer.setVisible(true);
        dialogContainer.setManaged(true);
    }

    @FXML
    public void initialize() {
        setupGrid(boardGridPlayer, Color.web("#0066CC"), false);
        setupGrid(boardGridOpponent, Color.web("#FF6600"), true);

        playerBoardContainer = (VBox) boardGridPlayer.getParent();
        opponentBoardContainer = (VBox) boardGridOpponent.getParent();

        playerResult = new GameResult(0, 0, 0, 0, 0, 0);
        playerResult.setPlayerName(UserSession.getInstance().getUsername());
        AIResult = new GameResult(0, 0, 0, 0, 0, 0);
        AIResult.setPlayerName("Computer");

        // Tạo danh sách ô để AI bắn
        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 10; col++) {
                availableTargets.add(new int[]{col, row});
            }
        }

        dialogContainer.setVisible(false);
        dialogContainer.setManaged(false);
        playerName.setText("You");
    }

    public void setPlayerTurn(boolean turn) {
        this.isPlayerTurn = turn;
    }

    public void setPlayerShips(List<Ship> playerShips) {
        this.playerShips = playerShips;
        ShipFactory shipFactory = new ShipFactory();
        this.opponentShips = shipFactory.getRandomShips();
        drawShips(boardGridPlayer, playerShips, Color.web("#0066CC"));
        updateTurnUI();
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

    private void drawShips(GridPane grid, List<Ship> ships, Color shipColor) {
        for (Ship ship : ships) {
            for (int i = 0; i < ship.getSize(); i++) {
                Rectangle rect = new Rectangle(30, 30);
                rect.setFill(Color.web("#90A4AE")); // Xám xanh đậm
                rect.setStroke(Color.web("#388E3C"));

                if (ship.isHorizontal()) {
                    grid.add(rect, ship.getStartX() + i, ship.getStartY());
                } else {
                    grid.add(rect, ship.getStartX(), ship.getStartY() + i);
                }
            }
        }
    }

    private void attack(int col, int row, Rectangle cell) {
        turn++;
        SoundManager.getInstance().playSound("src/main/resources/sounds/sound.mp3");
        boolean success = false;
        boolean sunk = false;

        for (Ship ship : opponentShips) {
            if (ship.isOccupied(col, row)) {
                ship.Attacked();
                success = true;
                if (ship.getAttackCount() == ship.getSize()) sunk = true;
                break;
            }
        }

        cell.setFill(success ? Color.web("#81C784") : Color.web("#FF5252"));
        cell.setOnMouseClicked(null);
        cell.setOnMouseEntered(null);  // ❗ Ngăn hover làm đổi màu
        cell.setOnMouseExited(null);   // ❗ Trả về đúng màu đã bắn

        updateGameResult(success, playerResult);
        if (sunk && playerResult.getHits() != 17) handleShipSunk();

        if (playerResult.getHits() == 17) {
            endGame(true);
            return;
        }
        if (turn == 1 && success) playerResult.setFirstHit(true);

        if (!success) {
            switchTurn();
            new Thread(() -> {
                try {
                    Thread.sleep(800);
                    aiTurn();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    private List<int[]> trackingHits = new ArrayList<>();
    private Queue<int[]> pendingHits = new LinkedList<>();
    private int[] currentDirection = null;
    private List<int[]> directionsToTry = new ArrayList<>();


    private void aiTurn() {
        if (aiPaused || availableTargets.isEmpty()) return;

        int col = -1, row = -1;

        if (!trackingHits.isEmpty()) {
            if (trackingHits.size() == 1) {
                if (directionsToTry.isEmpty()) {
                    directionsToTry = new ArrayList<>(List.of(
                            new int[]{0, -1}, new int[]{0, 1},
                            new int[]{-1, 0}, new int[]{1, 0}
                    ));
                }

                while (!directionsToTry.isEmpty()) {
                    int[] dir = directionsToTry.remove(0);
                    int[] origin = trackingHits.get(0);
                    int nextCol = origin[0] + dir[0];
                    int nextRow = origin[1] + dir[1];
                    if (isValid(nextCol, nextRow)) {
                        col = nextCol;
                        row = nextRow;
                        currentDirection = dir;
                        break;
                    }
                }

            } else {
                if (currentDirection == null && trackingHits.size() >= 2) {
                    int[] first = trackingHits.get(0);
                    int[] second = trackingHits.get(1);
                    currentDirection = new int[]{
                            second[0] - first[0],
                            second[1] - first[1]
                    };
                }

                if (currentDirection != null) {
                    int[] last = trackingHits.get(trackingHits.size() - 1);
                    int nextCol = last[0] + currentDirection[0];
                    int nextRow = last[1] + currentDirection[1];

                    if (isValid(nextCol, nextRow)) {
                        col = nextCol;
                        row = nextRow;
                    } else {
                        int[] first = trackingHits.get(0);
                        int revCol = first[0] - currentDirection[0];
                        int revRow = first[1] - currentDirection[1];

                        if (isValid(revCol, revRow)) {
                            Collections.reverse(trackingHits);
                            currentDirection = new int[]{
                                    -currentDirection[0],
                                    -currentDirection[1]
                            };
                            col = revCol;
                            row = revRow;
                        } else {
                            // Quay lại thử hướng khác
                            if (!trackingHits.isEmpty()) {
                                int[] origin = trackingHits.get(0);
                                trackingHits.clear();
                                trackingHits.add(origin);

                                // reset lại nhưng loại bỏ hướng sai
                                directionsToTry = new ArrayList<>(List.of(
                                        new int[]{0, -1}, new int[]{0, 1},
                                        new int[]{-1, 0}, new int[]{1, 0}
                                ));
                                directionsToTry.removeIf(dir -> Arrays.equals(dir, currentDirection));
                                currentDirection = null;

                                new Thread(() -> {
                                    try {
                                        Thread.sleep(100);
                                        aiTurn();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (col == -1 || row == -1) {
            if (!pendingHits.isEmpty()) {
                int[] nextHit = pendingHits.poll();
                if (nextHit != null && !visited.contains(nextHit[0] + "," + nextHit[1])) {
                    trackingHits.clear();
                    trackingHits.add(nextHit);
                    currentDirection = null;
                    directionsToTry.clear();

                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                            aiTurn();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    return;
                }
            }

            List<int[]> filtered = new ArrayList<>();
            for (int[] coord : availableTargets) {
                if ((coord[0] + coord[1]) % 2 == 0) {
                    filtered.add(coord);
                }
            }

            int[] coord = (filtered.isEmpty() ? availableTargets : filtered)
                    .remove(random.nextInt(filtered.isEmpty() ? availableTargets.size() : filtered.size()));

            col = coord[0];
            row = coord[1];
        }

        String key = col + "," + row;
        if (visited.contains(key)) {
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    aiTurn();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            return;
        }
        visited.add(key);

        boolean success = false;
        boolean sunk = false;

        for (Ship ship : playerShips) {
            if (ship.isOccupied(col, row)) {
                ship.Attacked();
                success = true;
                if (ship.getAttackCount() == ship.getSize()) {
                    sunk = true;
                    if (countHits(playerShips) != 17) {
                        Platform.runLater(this::handleShipSunk);
                    }
                }
                break;
            }
        }

        updateGameResult(success, AIResult);

        final boolean finalSuccess = success;
        final boolean finalSunk = sunk;
        final int finalCol = col;
        final int finalRow = row;

        Platform.runLater(() -> {
            Rectangle rect = getRectangleAt(boardGridPlayer, finalCol, finalRow);
            if (!finalSuccess) {
                if (rect != null) rect.setFill(Color.web("#FF5252"));

                if (currentDirection != null && !trackingHits.isEmpty()) {
                    int[] first = trackingHits.get(0);
                    int revCol = first[0] - currentDirection[0];
                    int revRow = first[1] - currentDirection[1];
                    if (isValid(revCol, revRow)) {
                        Collections.reverse(trackingHits);
                        currentDirection = new int[]{
                                -currentDirection[0],
                                -currentDirection[1]
                        };
                    } else {
                        if (!trackingHits.isEmpty()) {
                            int[] origin = trackingHits.get(0);
                            trackingHits.clear();
                            trackingHits.add(origin);

                            directionsToTry = new ArrayList<>(List.of(
                                    new int[]{0, -1}, new int[]{0, 1},
                                    new int[]{-1, 0}, new int[]{1, 0}
                            ));
                            directionsToTry.removeIf(dir -> Arrays.equals(dir, currentDirection));
                            currentDirection = null;
                        }
                    }
                }
            } else {
                if (rect != null) boardGridPlayer.getChildren().remove(rect);

                Rectangle newRect = new Rectangle(30, 30);
                newRect.setFill(Color.web("#FFF176"));
                newRect.setStroke(Color.web("#388E3C"));
                newRect.setArcWidth(4);
                newRect.setArcHeight(4);
                boardGridPlayer.add(newRect, finalCol, finalRow);

                if (!finalSunk) {
                    trackingHits.add(new int[]{finalCol, finalRow});
                } else {
                    trackingHits.clear();
                    currentDirection = null;
                    directionsToTry.clear();
                }
            }

            if (countHits(playerShips) == 17) {
                endGame(false);
            } else {
                if (finalSuccess) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(700);
                            aiTurn();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    switchTurn();
                }
            }
        });
    }

    private boolean isValid(int col, int row) {
        return col >= 1 && col <= 10 && row >= 1 && row <= 10 && !visited.contains(col + "," + row);
    }

    private Rectangle getRectangleAt(GridPane grid, int col, int row) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row && node instanceof Rectangle) {
                return (Rectangle) node;
            }
        }
        return null;
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
                    "-fx-border-color: #F44336; -fx-border-width: 2px; -fx-border-radius: 8px; " +
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

    private void updateGameResult(boolean success, GameResult gameResult) {
        int sunk = (int) opponentShips.stream().filter(ship -> ship.getAttackCount() == ship.getSize()).count();
        int playerSunk = (int) playerShips.stream().filter(ship -> ship.getAttackCount() == ship.getSize()).count();

        if (gameResult.equals(playerResult)) gameResult.setShipsDestroyed(sunk);
        else gameResult.setShipsDestroyed(playerSunk);
        if (success) {
            gameResult.setHits(gameResult.getHits() + 1);
            streak++;
            gameResult.setBestStreak(Math.max(streak, gameResult.getBestStreak()));
        } else {
            gameResult.setMisses(gameResult.getMisses() + 1);
            streak = 0;
        }

        int totalShots = gameResult.getHits() + gameResult.getMisses();
        int acc = totalShots > 0 ? gameResult.getHits() * 100 / totalShots : 0;
        gameResult.setAccuracy(acc);
        gameResult.setScore(gameResult.getBestStreak() * 3 + gameResult.getHits() + acc);
    }

    private int countHits(List<Ship> ships) {
        return ships.stream().mapToInt(Ship::getAttackCount).sum();
    }

    private void endGame(boolean win) {
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
                controller.setGameResults(playerResult, AIResult, turn);

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

    private void handleShipSunk() {
        aiPaused = true; // 🔴 Dừng AI hoàn toàn

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

                            new Thread(() -> {
                                try {
                                    Thread.sleep(400);
                                    Platform.runLater(() -> {
                                        aiPaused = false;
                                        if (!isPlayerTurn)
                                            aiTurn();
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
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
}

class ShipFactory {
    public Random random = new Random();
    private List<Ship> ships = new ArrayList<>();

    public List<Ship> getRandomShips() {
        placeShipsRandomly();
        return this.ships;
    }

    private void placeShipsRandomly() {
        int[] sizes = {5, 4, 3, 3, 2};
        for (int size : sizes) {
            boolean placed = false;
            while (!placed) {
                int x = random.nextInt(10) + 1;
                int y = random.nextInt(10) + 1;
                boolean horizontal = random.nextBoolean();
                if (canPlaceShip(x, y, size, horizontal, null)) {
                    Ship ship = new Ship(x, y, size, horizontal);
                    ships.add(ship);
                    placed = true;
                }
            }
        }
    }

    private boolean canPlaceShip(int x, int y, int size, boolean horizontal, Ship ignore) {
        if (horizontal) {
            if (x + size > 11) return false;
            for (int i = 0; i < size; i++) {
                if (isOccupied(x + i, y, ignore)) return false;
            }
        } else {
            if (y + size > 11) return false;
            for (int i = 0; i < size; i++) {
                if (isOccupied(x, y + i, ignore)) return false;
            }
        }
        return true;
    }

    private boolean isOccupied(int x, int y, Ship ignore) {
        for (Ship ship : ships) {
            if (ship != ignore && ship.isOccupied(x, y)) return true;
        }
        return false;
    }
}