package battleships;

import javafx.animation.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WinViewController extends Application {

    @FXML
    private Label WinLabel;

    @FXML
    private Region backgroundRegion;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/WinView.fxml"));
        BorderPane root = loader.load();
        primaryStage.setTitle("Win");
        primaryStage.setScene(new Scene(root, 1300, 750));
        primaryStage.show();
    }

    public void initialize() {
        if (WinLabel == null || backgroundRegion == null) {
            System.err.println("Error: FXML components not initialized.");
            return;
        }

        backgroundRegion.setPrefWidth(Double.MAX_VALUE);
        backgroundRegion.setPrefHeight(WinLabel.getHeight());

        WinLabel.setTranslateX(600);
        WinLabel.setOpacity(0);

        TranslateTransition moveToCenter = new TranslateTransition(Duration.seconds(1), WinLabel);
        moveToCenter.setToX(0);
        moveToCenter.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), WinLabel);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));

        TranslateTransition moveToLeft = new TranslateTransition(Duration.seconds(1), WinLabel);
        moveToLeft.setToX(-600);
        moveToLeft.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), WinLabel);
        fadeOut.setToValue(0);

        SequentialTransition animation = new SequentialTransition(
                new ParallelTransition(moveToCenter, fadeIn),
                pause,
                new ParallelTransition(moveToLeft, fadeOut)
        );

        animation.setOnFinished(event -> WinLabel.setVisible(false));
        animation.play();
    }
}
