package battleships;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import static battleships.Main.popScene;

public class WaitViewController {
    @FXML
    private Button btnBack;

    @FXML
    private void handleBack() {
        Scene previousScene = popScene();
        if (previousScene != null) {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(previousScene);
        }
    }

    @FXML
    private void handleNext() {

    }

    @FXML
    private ImageView shipTwo, shipThree1, shipThree2, shipFour, shipFive;
    @FXML
    private ImageView shipTwoFoe, shipThree1Foe, shipThree2Foe, shipFourFoe, shipFiveFoe, imageWait;
    @FXML
    private VBox avatarFoe, avatarWait;
    @FXML
    private Text textFoeWaiting, textFoe;
    @FXML
    private Button btnNext;

    @FXML
    public void initialize() {
        avatarFoe.setVisible(false);
        textFoe.setVisible(false);
        textFoe.setManaged(false);
        btnNext.setDisable(true);
        btnNext.setOpacity(0.7);

        animateDots();
        rotateAvatar();

        Platform.runLater(() -> {
            if (btnBack.getScene() != null) {
                listenForSceneChanges();
            } else {
                btnBack.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        listenForSceneChanges();
                    }
                });
            }
        });
    }

    private void animateDots() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> textFoeWaiting.setText("Waiting for opponent")),
                new KeyFrame(Duration.seconds(1), e -> textFoeWaiting.setText("Waiting for opponent.")),
                new KeyFrame(Duration.seconds(1.5), e -> textFoeWaiting.setText("Waiting for opponent..")),
                new KeyFrame(Duration.seconds(2), e -> textFoeWaiting.setText("Waiting for opponent..."))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void rotateAvatar() {
        RotateTransition rotate = new RotateTransition(Duration.seconds(3), imageWait);
        rotate.setAxis(javafx.geometry.Point3D.ZERO.add(0, 1, 0)); // Xoay quanh trục Y
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE); // Lặp vô hạn
        rotate.play();
    }

    private void listenForSceneChanges() {
        Stage stage = (Stage) btnBack.getScene().getWindow();

        if (stage.isMaximized()) {
            setLargeSize();
        } else {
            setSmallSize();
        }

        // 🔹 Đăng ký listener một lần duy nhất nếu chưa có
        stage.maximizedProperty().addListener((obs, wasMax, isNowMax) -> {
            System.out.println("Maximized: " + isNowMax);
            if (isNowMax) {
                setLargeSize();
            } else {
                setSmallSize();
            }
        });

        System.out.println("Scene đã sẵn sàng, kích hoạt resize listener!");
    }

    @FXML
    public void setLargeSize() {
        shipTwo.setFitWidth(50);
        shipTwo.setFitHeight(95);

        shipThree1.setFitHeight(145);
        shipThree1.setFitWidth(50);
        shipThree2.setFitHeight(145);
        shipThree2.setFitWidth(50);

        shipFour.setFitHeight(195);
        shipFour.setFitWidth(50);

        shipFive.setFitHeight(220);
        shipFive.setFitWidth(50);

        shipTwoFoe.setFitWidth(50);
        shipTwoFoe.setFitHeight(95);

        shipThree1Foe.setFitHeight(145);
        shipThree1Foe.setFitWidth(50);
        shipThree2Foe.setFitHeight(145);
        shipThree2Foe.setFitWidth(50);

        shipFourFoe.setFitHeight(195);
        shipFourFoe.setFitWidth(50);

        shipFiveFoe.setFitHeight(220);
        shipFiveFoe.setFitWidth(50);

    }


    public void setSmallSize() {
        shipTwo.setFitHeight(60);
        shipTwo.setFitWidth(30);

        shipThree1.setFitHeight(90);
        shipThree1.setFitWidth(30);
        shipThree2.setFitHeight(90);
        shipThree2.setFitWidth(30);

        shipFour.setFitHeight(120);
        shipFour.setFitWidth(30);

        shipFive.setFitHeight(150);
        shipFive.setFitWidth(30);

        shipTwoFoe.setFitHeight(60);
        shipTwoFoe.setFitWidth(30);

        shipThree1Foe.setFitHeight(90);
        shipThree1Foe.setFitWidth(30);
        shipThree2Foe.setFitHeight(90);
        shipThree2Foe.setFitWidth(30);

        shipFourFoe.setFitHeight(120);
        shipFourFoe.setFitWidth(30);

        shipFiveFoe.setFitHeight(150);
        shipFiveFoe.setFitWidth(30);
    }
}
