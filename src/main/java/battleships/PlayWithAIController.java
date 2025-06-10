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
import java.util.*;

public class PlayWithAIController extends Application {

    @FXML private GridPane boardGridPlayer;
    @FXML private GridPane boardGridOpponent;
    @FXML private Label playerLabel;
    @FXML private Label opponentLabel;
    @FXML private Label playerName;

    private VBox playerBoardContainer;
    private VBox opponentBoardContainer;

    private List<Ship> playerShips;
    private List<Ship> opponentShips;
    private List<int[]> availableTargets = new ArrayList<>();
    private boolean isPlayerTurn = true;

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

        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setPrefWidth(30);
        grid.getColumnConstraints().add(labelCol);

        for (int i = 0; i < 10; i++) {
            ColumnConstraints col = new ColumnConstraints(30);
            grid.getColumnConstraints().add(col);
            Label colLabel = new Label(String.valueOf(i + 1));
            colLabel.setMinSize(30, 30);
            colLabel.setAlignment(Pos.CENTER);
            grid.add(colLabel, i + 1, 0);
        }

        RowConstraints labelRow = new RowConstraints(30);
        grid.getRowConstraints().add(labelRow);
        for (int i = 0; i < 10; i++) {
            RowConstraints row = new RowConstraints(30);
            grid.getRowConstraints().add(row);
            Label rowLabel = new Label(String.valueOf((char) ('A' + i)));
            rowLabel.setMinSize(30, 30);
            rowLabel.setAlignment(Pos.CENTER);
            grid.add(rowLabel, 0, i + 1);
        }

        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 10; col++) {
                Rectangle cell = new Rectangle(30, 30);
                cell.setFill(canClick ? Color.web("#E1F5FE") : Color.web("#E8F5E8"));
                cell.setStroke(strokeColor);
                cell.setStrokeWidth(1);

                final int r = row, c = col;
                if (canClick) {
                    cell.setOnMouseClicked(e -> {
                        if (isPlayerTurn) attack(c, r, cell);
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
        updateGameResult(success, playerResult);
        if (sunk && playerResult.getHits() != 17) handleShipSunk();

        if (playerResult.getHits() == 17) {
            endGame(true);
            return;
        }

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

    private void aiTurn() {
        if (availableTargets.isEmpty()) return;

        int[] coord = availableTargets.remove(random.nextInt(availableTargets.size()));
        int col = coord[0], row = coord[1];

        boolean success = false;
        boolean sunk = false;

        for (Ship ship : playerShips) {
            if (ship.isOccupied(col, row)) {
                ship.Attacked();
                success = true;
                if (ship.getAttackCount() == ship.getSize()) sunk = true;
                break;
            }
        }

        updateGameResult(success, AIResult);
        if (sunk && countHits(playerShips) != 17) handleShipSunk();

        final boolean finalSuccess = success;

        Platform.runLater(() -> {
            Rectangle rect = getRectangleAt(boardGridPlayer, col, row);

            if (!finalSuccess) {
                if (rect != null) rect.setFill(Color.web("#FF5252"));
            } else {
                if (rect != null) {
                    boardGridPlayer.getChildren().remove(rect);
                }

                Rectangle newRect = new Rectangle(30, 30);
                newRect.setFill(Color.web("#FFF176"));
                newRect.setStroke(Color.web("#388E3C"));
                newRect.setArcWidth(4);
                newRect.setArcHeight(4);

                boardGridPlayer.add(newRect, col, row);
            }

            if (countHits(playerShips) == 17) {
                endGame(false);
            } else if (!finalSuccess) {
                switchTurn(); // chuyển về người chơi
            } else {
                // bắn trúng, tiếp tục bắn sau delay
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        aiTurn(); // gọi lại đệ quy
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
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
        playerLabel.setText(isPlayerTurn ? "🔰 YOUR FLEET - DEFENDING" : "🔱 UNDER ATTACK!");
        opponentLabel.setText(isPlayerTurn ? "🎯 YOUR TURN - ATTACK!" : "🏴‍☠️ ENEMY ATTACKING");
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