package battleships;

import javafx.animation.PauseTransition;
import client.NetworkManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import static battleships.Main.pushScene;

public class ResultViewController extends Application {
    private NetworkManager network;

    public void setNetwork(NetworkManager network) {
        this.network = network;
    }

    @FXML
    private Label turnsLabel;
    @FXML
    private TextFlow shipsDestroyedLeft, hitsLeft, missesLeft, accuracyLeft, streakLeft, scoreLeft;
    @FXML
    private TextFlow shipsDestroyedRight, hitsRight, missesRight, accuracyRight, streakRight, scoreRight;
    @FXML
    private Button nextButton;
    @FXML
    private Label playerNameLabel;
    @FXML
    private Label opponentNameLabel;

    private GameResult leftPlayer;
    private GameResult rightPlayer;
    private int totalTurns;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResultView.fxml"));
        BorderPane root = loader.load();

        ResultViewController controller = loader.getController();
        controller.setGameResults(
                new GameResult(4, 15, 29, 34, 4, 121),
                new GameResult(5, 17, 29, 36, 5, 121),
                58
        );

        Scene scene = new Scene(root, 1300, 750);
        stage.setScene(scene);
        stage.setTitle("Game Result");
        stage.show();
    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void handleNext() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EndGameView.fxml"));
            Parent endGameView = loader.load();

            EndGameController endGameController = loader.getController();
            endGameController.setNetwork(network);

            Stage stage = (Stage) nextButton.getScene().getWindow();
            Scene currentScene = stage.getScene();
            pushScene(currentScene);
            stage.setScene(new Scene(endGameView));
            stage.show();

            Platform.runLater(() -> {
                if (leftPlayer != null) {
                    boolean isWin = leftPlayer.getShipsDestroyed() == 5;
                    endGameController.winInfo(isWin, leftPlayer.getHits(), leftPlayer.getHits() + leftPlayer.getMisses());
                }

                PauseTransition pause1 = new PauseTransition(Duration.seconds(0.3));
                pause1.setOnFinished(event1 -> {
                    endGameController.updateProgressBar1();

                    // Kiểm tra điều kiện chạy progressBar2
                    if (leftPlayer != null && leftPlayer.isFirstHit()) {
                        PauseTransition pause2 = new PauseTransition(Duration.seconds(1));
                        pause2.setOnFinished(event2 -> {
                            endGameController.updateProgressBar2();

                            // Sau progressBar2 → đến progressBar3 (nếu cần)
                            if (leftPlayer.getAccuracy() >= 35) {
                                PauseTransition pause3 = new PauseTransition(Duration.seconds(1));
                                pause3.setOnFinished(event3 -> {
                                    endGameController.updateProgressBar3();
                                });
                                pause3.play();
                            }
                        });
                        pause2.play();
                    } else {
                        // Nếu bỏ qua progressBar2 → xét luôn progressBar3
                        if (leftPlayer != null && leftPlayer.getAccuracy() >= 35) {
                            PauseTransition pause3 = new PauseTransition(Duration.seconds(1));
                            pause3.setOnFinished(event3 -> {
                                endGameController.updateProgressBar3();
                            });
                            pause3.play();
                        }
                    }
                });
                pause1.play();
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading EndGame view: " + e.getMessage());
        }
    }



    public void setGameResults(GameResult left, GameResult right, int turns) {

        this.leftPlayer = left;
        this.rightPlayer = right;
        this.totalTurns = turns;

        playerNameLabel.setText(left.getPlayerName());
        opponentNameLabel.setText(right.getPlayerName());

        turnsLabel.setText(String.valueOf(turns));
        if (right == null) {
            right = new GameResult(0, 0, 0, 0, 0, 0);
        }
        if (left == null) {
            left = new GameResult(0, 0, 0, 0, 0, 0);
        }
        updateTextFlow(shipsDestroyedLeft, left.getShipsDestroyed() + "/5");
        updateTextFlow(hitsLeft, String.valueOf(left.getHits()));
        updateTextFlow(missesLeft, String.valueOf(left.getMisses()));
        updateTextFlow(accuracyLeft, left.getAccuracy() + "%");
        updateTextFlow(streakLeft, String.valueOf(left.getBestStreak()));
        updateTextFlow(scoreLeft, String.valueOf(left.getScore()));

        updateTextFlow(shipsDestroyedRight, right.getShipsDestroyed() + "/5");
        updateTextFlow(hitsRight, String.valueOf(right.getHits()));
        updateTextFlow(missesRight, String.valueOf(right.getMisses()));
        updateTextFlow(accuracyRight, right.getAccuracy() + "%");
        updateTextFlow(streakRight, String.valueOf(right.getBestStreak()));
        updateTextFlow(scoreRight, String.valueOf(right.getScore()));
    }

    private void updateTextFlow(TextFlow textFlow, String newText) {
        if (!textFlow.getChildren().isEmpty() && textFlow.getChildren().get(0) instanceof Text) {
            ((Text) textFlow.getChildren().get(0)).setText(newText);
        }
    }


    public GameResult getLeftPlayer() {
        return leftPlayer;
    }

    public GameResult getRightPlayer() {
        return rightPlayer;
    }

    public int getTotalTurns() {
        return totalTurns;
    }
}