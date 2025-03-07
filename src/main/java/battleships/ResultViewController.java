package battleships;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ResultViewController extends Application {

    @FXML
    private Label turnsLabel;
    @FXML
    private TextFlow shipsDestroyedLeft, hitsLeft, missesLeft, accuracyLeft, streakLeft;
    @FXML
    private TextFlow shipsDestroyedRight, hitsRight, missesRight, accuracyRight, streakRight;



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResultView.fxml"));
        BorderPane root = loader.load();

        ResultViewController controller = loader.getController();
        controller.setGameResults(
                new GameResult(4, 15, 29, 34, 4),
                new GameResult(5, 17, 29, 36, 5),
                58
        );

        Scene scene = new Scene(root, 1300, 750);
        stage.setScene(scene);
        stage.setTitle("Game Result");
        stage.show();
    }

    public void setGameResults(GameResult left, GameResult right, int turns) {

        turnsLabel.setText(String.valueOf(turns));

        updateTextFlow(shipsDestroyedLeft, left.getShipsDestroyed() + "/5");
        updateTextFlow(hitsLeft, String.valueOf(left.getHits()));
        updateTextFlow(missesLeft, String.valueOf(left.getMisses()));
        updateTextFlow(accuracyLeft, left.getAccuracy() + "%");
        updateTextFlow(streakLeft, String.valueOf(left.getBestStreak()));


        updateTextFlow(shipsDestroyedRight, right.getShipsDestroyed() + "/5");
        updateTextFlow(hitsRight, String.valueOf(right.getHits()));
        updateTextFlow(missesRight, String.valueOf(right.getMisses()));
        updateTextFlow(accuracyRight, right.getAccuracy() + "%");
        updateTextFlow(streakRight, String.valueOf(right.getBestStreak()));
    }

    private void updateTextFlow(TextFlow textFlow, String newText) {
        if (!textFlow.getChildren().isEmpty() && textFlow.getChildren().get(0) instanceof Text) {
            ((Text) textFlow.getChildren().get(0)).setText(newText);
        }
    }
}
