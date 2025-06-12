package battleships;

import common.UserSession;
import database.DatabaseHelper;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static battleships.Main.popScene;
import static battleships.Main.pushScene;

public class BoardOfflineController extends Application {
    @FXML private GridPane boardGrid;
    @FXML private Button readyButton, btnSettings;

    private List<Ship> ships = new ArrayList<>();
    private Random random = new Random();
    private List<Rectangle> previousCells = new ArrayList<>();
    private Ship draggingShip = null;
    private boolean isReady = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BoardView.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Scene scene = new Scene(root, 1300, 750);
        primaryStage.setTitle("Battle Ships (Offline)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    public void initialize() {
        ships.clear();
        boardGrid.getChildren().clear();
        boardGrid.getColumnConstraints().clear();
        boardGrid.getRowConstraints().clear();

        ImageView settingsIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/setting.png")));
        settingsIcon.setFitWidth(57); // Kích thước icon
        settingsIcon.setFitHeight(57);
        btnSettings.setGraphic(settingsIcon);

        ColumnConstraints labelCol = new ColumnConstraints(45);
        boardGrid.getColumnConstraints().add(labelCol);

        for (int i = 0; i < 10; i++) {
            ColumnConstraints col = new ColumnConstraints(45);
            boardGrid.getColumnConstraints().add(col);
            Label label = new Label(String.valueOf(i + 1));
            label.setAlignment(Pos.CENTER);
            boardGrid.add(label, i + 1, 0);
        }

        RowConstraints labelRow = new RowConstraints(45);
        boardGrid.getRowConstraints().add(labelRow);
        for (int i = 0; i < 10; i++) {
            RowConstraints row = new RowConstraints(45);
            boardGrid.getRowConstraints().add(row);
            Label label = new Label(String.valueOf((char) ('A' + i)));
            label.setPadding(new Insets(0, 0, 0, 25));
            boardGrid.add(label, 0, i + 1);
        }

        drawGrid();
        placeShipsRandomly();

        boardGrid.setOnDragOver(this::handleDragOver);
        boardGrid.setOnDragDropped(this::handleDragDropped);
    }

    private void drawGrid() {
        boardGrid.getChildren().removeIf(node -> node instanceof Rectangle);

        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 10; col++) {
                Rectangle cell = new Rectangle(45, 45);
                cell.setFill(Color.color(1, 1, 1, 0.5));
                cell.setStroke(Color.BLACK);
                boardGrid.add(cell, col, row);
            }
        }
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
                    addShipToGrid(ship);
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

    @FXML
    private void handleBack()  throws IOException{
        UserSession session = UserSession.getInstance();
        DatabaseHelper.updateSession(session.getUsername());
        LoginController.getPlayerDailyQuests(session.getUserId());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Parent modeView = loader.load();

        // Gọi lại update khi cửa sổ mở lại
        MainController controller = loader.getController();
        controller.updateInfoPersonal();
        controller.updateDailyQuests();

        Stage stage = (Stage) readyButton.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }

    private void addShipToGrid(Ship ship) {
        Image defaultShipImage = new Image(getClass().getResourceAsStream("/images/icon_ship.png"));
        Image greenShipImage = new Image(getClass().getResourceAsStream("/images/icon_ship_green.png"));
        Image redShipImage = new Image(getClass().getResourceAsStream("/images/icon_ship_red.png"));
        Image blueShipImage = new Image(getClass().getResourceAsStream("/images/icon_ship_blue.png"));
        Image orangeShipImage = new Image(getClass().getResourceAsStream("/images/icon_ship_orange.png"));

        // Chọn ảnh phù hợp theo kích thước tàu
        ImagePattern shipPattern;
        switch (ship.getSize()) {
            case 5:
                shipPattern = new ImagePattern(greenShipImage);
                break;
            case 4:
                shipPattern = new ImagePattern(redShipImage);
                break;
            case 3:
                shipPattern = new ImagePattern(blueShipImage);
                break;
            case 2:
                shipPattern = new ImagePattern(orangeShipImage);
                break;
            default:
                shipPattern = new ImagePattern(defaultShipImage);
        }
        for (int i = 0; i < ship.getSize(); i++) {
            Rectangle rect = new Rectangle(45, 45);
            rect.setFill(shipPattern);
            rect.setStroke(Color.BLACK);

            int col = ship.isHorizontal() ? ship.getStartX() + i : ship.getStartX();
            int row = ship.isHorizontal() ? ship.getStartY() : ship.getStartY() + i;

            rect.setOnMouseClicked(e -> {
                if (e.getClickCount() == 1) {
                    ship.toggleOrientation();
                    if (!canPlaceShip(ship.getStartX(), ship.getStartY(), ship.getSize(), ship.isHorizontal(), ship))
                        ship.toggleOrientation();
                    updateBoard();
                }
            });

            rect.setOnDragDetected(e -> {
                Dragboard db = rect.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("drag");
                db.setContent(content);
                draggingShip = ship;
                updateBoard();
                e.consume();
            });

            boardGrid.add(rect, col, row);
        }
    }

    private void handleDragOver(DragEvent event) {
        if (draggingShip == null) return;
        Node node = event.getPickResult().getIntersectedNode();
        if (!(node instanceof Rectangle)) return;

        Integer newX = GridPane.getColumnIndex(node);
        Integer newY = GridPane.getRowIndex(node);
        if (newX == null || newY == null) return;

        resetPreviousCellsColor();
        boolean isValid = canPlaceShip(newX, newY, draggingShip.getSize(), draggingShip.isHorizontal(), draggingShip);
        previousCells.clear();

        for (int i = 0; i < draggingShip.getSize(); i++) {
            int checkX = draggingShip.isHorizontal() ? newX + i : newX;
            int checkY = draggingShip.isHorizontal() ? newY : newY + i;
            Rectangle cell = getRectangleAt(checkX, checkY);
            if (cell != null) {
                cell.setFill(isValid ? Color.GREEN : Color.RED);
                previousCells.add(cell);
            }
        }

        event.acceptTransferModes(TransferMode.MOVE);
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        if (!(node instanceof Rectangle) || draggingShip == null) return;

        int newX = GridPane.getColumnIndex(node);
        int newY = GridPane.getRowIndex(node);

        if (canPlaceShip(newX, newY, draggingShip.getSize(), draggingShip.isHorizontal(), draggingShip)) {
            draggingShip.setPosition(newX, newY);
        }

        draggingShip = null;
        updateBoard();
    }

    private void resetPreviousCellsColor() {
        for (Rectangle cell : previousCells) {
            cell.setFill(Color.color(1, 1, 1, 0.5));
        }
        previousCells.clear();
    }

    private Rectangle getRectangleAt(int x, int y) {
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof Rectangle &&
                    GridPane.getColumnIndex(node) == x &&
                    GridPane.getRowIndex(node) == y) {
                return (Rectangle) node;
            }
        }
        return null;
    }

    private void updateBoard() {
        drawGrid();
        for (Ship ship : ships) {
            addShipToGrid(ship);
        }
    }

    @FXML
    public void Ready() {
        isReady = true;
        readyButton.setText("Ready");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlayWithAIView.fxml"));
            Parent playView = loader.load();
            PlayWithAIController controller = loader.getController();

            controller.setPlayerTurn(true);
            controller.setPlayerShips(ships);

            Stage stage = (Stage) readyButton.getScene().getWindow();
            Scene currentScene = stage.getScene();
            pushScene(currentScene);
            stage.setScene(new Scene(playView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSetting()  throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SettingView.fxml"));
        Parent modeView = loader.load();

        Stage stage = (Stage) readyButton.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }
}
