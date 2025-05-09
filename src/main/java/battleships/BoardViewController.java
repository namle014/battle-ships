package battleships;

import client.NetworkManager;
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
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static battleships.Main.pushScene;

public class BoardViewController extends Application {
    NetworkManager network;
    public void setNetwork(NetworkManager network) {
        this.network = network;
    }

    @FXML
    private GridPane boardGrid;
    @FXML
    private Button readyButton;

    private List<Ship> ships = new ArrayList<>();
    private Random random = new Random();
    private List<Rectangle> previousCells = new ArrayList<>();
    private Ship draggingShip = null;
    private boolean isReady = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BoardView.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root, 1300, 750);
        primaryStage.setTitle("Battle Ships");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void initialize() {
        ships.clear();
        boardGrid.getChildren().clear();
        boardGrid.getColumnConstraints().clear();
        boardGrid.getRowConstraints().clear();

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPrefWidth(45);
        boardGrid.getColumnConstraints().add(column1);

        for (int i = 0; i < 10; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(45);
            boardGrid.getColumnConstraints().add(column);

            Label colLabel = new Label(String.valueOf(i + 1));
            colLabel.setStyle("-fx-font-weight: bold;");
            colLabel.setMinSize(45, 45);
            colLabel.setAlignment(Pos.BOTTOM_CENTER);
            GridPane.setHalignment(colLabel, javafx.geometry.HPos.CENTER);
            boardGrid.add(colLabel, i + 1, 0);
        }

        RowConstraints row1 = new RowConstraints();
        row1.setPrefHeight(45);
        boardGrid.getRowConstraints().add(row1);

        for (int i = 0; i < 10; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(45);
            boardGrid.getRowConstraints().add(row);

            Label rowLabel = new Label(String.valueOf((char) ('A' + i)));
            rowLabel.setStyle("-fx-font-weight: bold;");
            rowLabel.setMinSize(45, 45);
            rowLabel.setPadding(new Insets(0, 0, 0, 25));
            rowLabel.setAlignment(Pos.CENTER_LEFT);
            GridPane.setHalignment(rowLabel, javafx.geometry.HPos.LEFT);
            boardGrid.add(rowLabel, 0, i + 1);
        }

        drawGrid();
        placeShipsRandomly();

        boardGrid.setOnDragOver(this::handleDragOver);
        boardGrid.setOnDragDropped(this::handleDragDropped);
        boardGrid.setOnDragDone(event -> updateBoard());
    }



    private void drawGrid() {
        boardGrid.getChildren().removeIf(node -> node instanceof Rectangle);

        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 10; col++) {
                Rectangle cell = new Rectangle(45, 45);
                cell.setFill(Color.color(1.0, 1.0, 1.0, 0.5));
                cell.setStroke(Color.BLACK);
                cell.setStrokeWidth(1);
                boardGrid.add(cell, col, row);
            }
        }
    }


    private void placeShipsRandomly() {
        int[] shipSizes = {5, 4, 3, 3, 2};
        for (int size : shipSizes) {
            placeShip(size);
        }
    }

    private void placeShip(int size) {
        boolean placed = false;
        while (!placed) {
            int startX = random.nextInt(10)+1;
            int startY = random.nextInt(10)+1;
            boolean horizontal = random.nextBoolean();

            if (canPlaceShip(startX, startY, size, horizontal, null)) {
                Ship newShip = new Ship(startX, startY, size, horizontal);
                ships.add(newShip);
                addShipToGrid(newShip);
                placed = true;
            }
        }
    }

    private boolean canPlaceShip(int startX, int startY, int size, boolean horizontal, Ship currentShip) {
        if (horizontal) {
            if (startX + size > 11) return false;
            for (int i = 0; i < size; i++) {
                if (isOccupied(startX + i, startY, currentShip)) return false;
            }
        } else {
            if (startY + size > 11) return false;
            for (int i = 0; i < size; i++) {
                if (isOccupied(startX, startY + i, currentShip)) return false;
            }
        }
        return true;
    }

    private boolean isOccupied(int x, int y, Ship currentShip) {
        for (Ship ship : ships) {
            if (ship != currentShip && ship.isOccupied(x, y)) {
                return true;
            }
        }
        return false;
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
            Rectangle rectangle = new Rectangle(45, 45);
            rectangle.setStroke(Color.BLACK);
            rectangle.setStrokeWidth(1);
            rectangle.setFill(shipPattern);

            if (ship.isHorizontal()) {
                boardGrid.add(rectangle, ship.getStartX() + i, ship.getStartY());
            } else {
                boardGrid.add(rectangle, ship.getStartX(), ship.getStartY() + i);
            }

            rectangle.setOnMouseClicked(event -> handleClick(event, ship));
            rectangle.setOnDragDetected(event -> handleDragDetected(event, ship));
            rectangle.setOnDragDone(event -> {
                if (!event.isDropCompleted()) {
                    updateBoard();
                }
                event.consume();
            });
        }
    }

    private void handleClick(MouseEvent event, Ship ship) {
        if (event.getClickCount() == 1) {
            ship.toggleOrientation();
            if (!canPlaceShip(ship.getStartX(), ship.getStartY(), ship.getSize(), ship.isHorizontal(), ship)) {
                ship.toggleOrientation();
            }
            Platform.runLater(this::updateBoard);
        }
    }

    private void handleDragDetected(MouseEvent event, Ship ship) {
        Dragboard db = ((Node) event.getSource()).startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(ship.getStartX() + "," + ship.getStartY());
        db.setContent(content);
        draggingShip = ship;
        drawGrid();
        for (Ship ship1 : ships) {
            if (ship1 != draggingShip) {
                addShipToGrid(ship1);
            }
        }
        event.consume();
    }


    private void updateShipVisibility(Ship ship) {
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof Rectangle) {
                int x = GridPane.getColumnIndex(node);
                int y = GridPane.getRowIndex(node);
                if (ship.isOccupied(x, y)) {
                    ((Rectangle) node).setFill(Color.TRANSPARENT);
                    ((Rectangle) node).setFill(Color.color(1.0, 1.0, 1.0, 0.5));
                }
            }
        }
    }


    private void handleDragOver(DragEvent event) {
        if (!event.getDragboard().hasString()) return;
        Node node = event.getPickResult().getIntersectedNode();
        if (!(node instanceof Rectangle)) {
            return;
        }
        Integer newX = GridPane.getColumnIndex(node);
        Integer newY = GridPane.getRowIndex(node);
        if (newX == null || newY == null) return;
        if (draggingShip == null) return;
        resetPreviousCellsColor();
        boolean isValid = canPlaceShip(newX, newY, draggingShip.getSize(), draggingShip.isHorizontal(), draggingShip);
        previousCells.clear();

        for (int i = 0; i < draggingShip.getSize(); i++) {
            int checkX = draggingShip.isHorizontal() ? newX + i : newX;
            int checkY = draggingShip.isHorizontal() ? newY : newY + i;

            if (checkX > 10 || checkY > 10) continue;

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
        if (draggingShip == null) return;

        Node node = event.getPickResult().getIntersectedNode();
        if (!(node instanceof Rectangle)) return;

        int newX = GridPane.getColumnIndex(node);
        int newY = GridPane.getRowIndex(node);

        if (canPlaceShip(newX, newY, draggingShip.getSize(), draggingShip.isHorizontal(), draggingShip)) {
            draggingShip.setPosition(newX, newY);
            updateBoard();
            event.setDropCompleted(true);
        } else {
            event.setDropCompleted(false);
        }
        updateBoard();
        draggingShip = null;
    }

    private void resetPreviousCellsColor() {
        for (Rectangle cell : previousCells) cell.setFill(Color.color(1.0, 1.0, 1.0, 0.5));
        previousCells.clear();
    }

    private Rectangle getRectangleAt(int x, int y) {
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof Rectangle && GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
                return (Rectangle) node;
            }
        }
        return null;
    }

    private void updateBoard() {
        drawGrid();
        for (Ship ship : ships) addShipToGrid(ship);
    }

    public void Ready(){
        isReady = !isReady;
        readyButton.setText(isReady ? "Ready" : "Not Ready");
        network.requestReady(isReady, ships);
    }

    public void startGame(boolean yourTurn, List<Ship> opponentShips) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlayGameView.fxml"));
        Parent playView = loader.load();
        PlayGameViewController controller = (PlayGameViewController) loader.getController();
        network.setPlayGameViewController(controller);
        controller.setNetwork(network);
        controller.setPlayerTurn(yourTurn);
        controller.setPlayerShips(ships, opponentShips);

        Stage stage = (Stage) readyButton.getScene().getWindow();
        stage.setScene(new Scene(playView));

        stage.show();
    }
}
