package battleships;

import client.NetworkManager;
import common.DailyQuestsSession;
import common.ShowFireworksLevelUp;
import common.UserSession;
import database.DatabaseHelper;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static battleships.Main.pushScene;

public class EndGameController {
    private NetworkManager network;

    public void setNetwork(NetworkManager network) {
        this.network = network;
    }

    @FXML
    private Button btnMainMenu;

    @FXML
    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4;

    @FXML
    private Label progressLabel4, progressLabel3, progressLabel2, progressLabel1;

    public void updateProgressBar3() {
        Timeline timeline = new Timeline();
        if (progressBar3.getProgress() >= 1) {
            return;
        }

        progressLabel3.setText("1/1");

        // Chia nhỏ tiến trình thành 100 bước (từ 0 -> 1)
        for (int i = 0; i <= 100; i++) {
            double progress = i / 100.0;
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 7), e -> progressBar3.setProgress(progress))
            );
        }

        timeline.setOnFinished(e -> {
            if (progressBar3.getProgress() >= 1) {
                animateMedal3Move();
            }
        });

        String sql = "UPDATE player_daily_quests SET current_progress = ? WHERE player_id = CAST(? AS UUID) AND quest_id = ?";

        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, 1);
            stmt.setObject(2, UUID.fromString(UserSession.getInstance().getUserId()));
            stmt.setInt(3, 3);

            stmt.executeUpdate();

            System.out.println("Successfully updated after level up");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        timeline.setCycleCount(1);
        timeline.play();
    }

    @FXML
    public void initialize() {
        updateInfoPersonal();
        updateDailyQuests();
    }

    public void updateInfoPersonal() {
        UserSession session = UserSession.getInstance();
        int level = session.getLevel();
        int currentProgress = session.getCurrentProgressLevel();

        progressBar4.setProgress(1.0 * currentProgress / Math.min(level * 4, 30));
        progressLabel4.setText(String.format("%d/%d", currentProgress, Math.min(level * 4, 30)));
    }

    public void updateDailyQuests() {
        List<DailyQuestsSession> session = DailyQuestsSession.getDailyQuests();
        DailyQuestsSession progress1 = session.get(0);
        DailyQuestsSession progress2 = session.get(1);
        DailyQuestsSession progress3 = session.get(2);

        progressBar1.setProgress(1.0 * progress1.getCurrentProgress() / 3);
        progressLabel1.setText(String.format("%d/3", progress1.getCurrentProgress()));

        progressBar2.setProgress(1.0 * progress2.getCurrentProgress() / 2);
        progressLabel2.setText(String.format("%d/2", progress2.getCurrentProgress()));

        progressBar3.setProgress(1.0 * progress3.getCurrentProgress());
        progressLabel3.setText(String.format("%d/1", progress3.getCurrentProgress()));
    }

    public void updateProgressBar2() {
        Timeline timeline = new Timeline();
        if (progressBar2.getProgress() >= 1) {
            return;
        }

        int current = Integer.parseInt(progressLabel2.getText().split("/")[0]);
        current++;

        progressLabel2.setText(String.format("%d/2", current));

        for (int i = (current - 1) * 50 + 1; i <= current * 50; i++) {
            double progress = i / 100.0;
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 7), e -> progressBar2.setProgress(progress))
            );
        }

        timeline.setOnFinished(e -> {
            if (progressBar2.getProgress() >= 1) {
                animateMedal2Move();
            }
        });

        String sql = "UPDATE player_daily_quests SET current_progress = ? WHERE player_id = CAST(? AS UUID) AND quest_id = ?";

        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, current);
            stmt.setObject(2, UUID.fromString(UserSession.getInstance().getUserId()));
            stmt.setInt(3, 2);

            stmt.executeUpdate();

            System.out.println("Successfully updated after level up");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        timeline.setCycleCount(1);
        timeline.play();
    }

    public void updateProgressBar1() {
        Timeline timeline = new Timeline();
        if (progressBar1.getProgress() >= 1) {
            return;
        }

        int current = Integer.parseInt(progressLabel1.getText().split("/")[0]);
        current++;

        progressLabel1.setText(String.format("%d/3", current));

        for (double i = 33 * (current - 1) + 1; i <= 33 * current + 1; i++) {
            double progress = i / 100.0;
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 7), e -> progressBar1.setProgress(progress))
            );
        }

        timeline.setOnFinished(e -> {
            if (progressBar1.getProgress() >= 1) {
                animateMedal1Move();
            }
        });

        String sql = "UPDATE player_daily_quests SET current_progress = ? WHERE player_id = CAST(? AS UUID) AND quest_id = ?";

        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, current);
            stmt.setObject(2, UUID.fromString(UserSession.getInstance().getUserId()));
            stmt.setInt(3, 1);

            stmt.executeUpdate();

            System.out.println("Successfully updated after level up");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        timeline.setCycleCount(1);
        timeline.play();
    }

    private void animateProgressTransfer(int received) {
        Timeline timeline = new Timeline();

        String text = progressLabel4.getText();

        int current =  Integer.parseInt(text.split("/")[0]);
        int total =  Integer.parseInt(text.split("/")[1]);

        double rate = 100.0 / total * current;

        for (double i = rate; i <= Math.min(100.0, rate + (1.0 * received * 100 / total)); i++) {
            double progress = i / 100.0;
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 7), e -> progressBar4.setProgress(progress))
            );
        }

        String sql = "UPDATE players SET current_progress_level = ? WHERE id = CAST(? AS UUID)";

        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int newProgress = current + received;
            stmt.setInt(1, newProgress);
            stmt.setObject(2, UUID.fromString(UserSession.getInstance().getUserId()));

            stmt.executeUpdate();

            System.out.println("Successfully updated after level up");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (current + received <= total) {
            progressLabel4.setText(Integer.toString(current + received) + '/' + Integer.toString(total));
        }
        else {
            progressLabel4.setText(Integer.toString(total) + '/' + Integer.toString(total));
        }

        timeline.setOnFinished(e -> {
            if (progressBar4.getProgress() >= 1) {
                showLevelUp((Pane) progressBar3.getScene().getRoot(), received + current, total);
            }
        });

        timeline.setCycleCount(1);
        timeline.play();
    }

    @FXML
    private void handleMainMenu() throws IOException {
        network.leaveRoom();
        UserSession session = UserSession.getInstance();
        DatabaseHelper.updateSession(session.getUsername());
        LoginController.getPlayerDailyQuests(session.getUserId());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Parent modeView = loader.load();

        // Gọi lại update khi cửa sổ mở lại
        MainController controller = loader.getController();
        controller.updateInfoPersonal();
        controller.updateDailyQuests();

        Stage stage = (Stage) btnMainMenu.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }

    @FXML
    private ImageView medal1, medal2, medal3, medal4;

    @FXML
    private void animateMedal3Move() {
        Bounds startBounds = medal3.localToScene(medal3.getBoundsInLocal());
        Bounds endBounds = medal4.localToScene(medal4.getBoundsInLocal());

        double startX = startBounds.getMinX();
        double startY = startBounds.getMinY();
        double endX = endBounds.getMinX();
        double endY = endBounds.getMinY();

        ImageView movingMedal = new ImageView(medal3.getImage());
        movingMedal.setFitWidth(medal3.getFitWidth());
        movingMedal.setFitHeight(medal3.getFitHeight());

        ((Pane) medal3.getParent()).getChildren().add(movingMedal);

        movingMedal.setLayoutX(startX);
        movingMedal.setLayoutY(startY);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), movingMedal);
        transition.setByX(endX - startX);
        transition.setByY(endY - startY);
        transition.setOnFinished(event -> {
            ((Pane) medal3.getParent()).getChildren().remove(movingMedal);
            animateProgressTransfer(2);
        });

        transition.play();
    }

    @FXML
    private void animateMedal2Move() {
        Bounds startBounds = medal2.localToScene(medal2.getBoundsInLocal());
        Bounds endBounds = medal4.localToScene(medal4.getBoundsInLocal());

        double startX = startBounds.getMinX();
        double startY = startBounds.getMinY();
        double endX = endBounds.getMinX();
        double endY = endBounds.getMinY();

        ImageView movingMedal = new ImageView(medal2.getImage());
        movingMedal.setFitWidth(medal2.getFitWidth());
        movingMedal.setFitHeight(medal2.getFitHeight());

        ((Pane) medal2.getParent()).getChildren().add(movingMedal);

        movingMedal.setLayoutX(startX);
        movingMedal.setLayoutY(startY);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.7), movingMedal);
        transition.setByX(endX - startX);
        transition.setByY(endY - startY);
        transition.setOnFinished(event -> {
            ((Pane) medal2.getParent()).getChildren().remove(movingMedal);
            animateProgressTransfer(2);
        });

        transition.play();
    }

    @FXML
    private void animateMedal1Move() {
        Bounds startBounds = medal1.localToScene(medal1.getBoundsInLocal());
        Bounds endBounds = medal4.localToScene(medal4.getBoundsInLocal());

        double startX = startBounds.getMinX();
        double startY = startBounds.getMinY();
        double endX = endBounds.getMinX();
        double endY = endBounds.getMinY();

        ImageView movingMedal = new ImageView(medal1.getImage());
        movingMedal.setFitWidth(medal1.getFitWidth());
        movingMedal.setFitHeight(medal1.getFitHeight());

        ((Pane) medal1.getParent()).getChildren().add(movingMedal);

        movingMedal.setLayoutX(startX);
        movingMedal.setLayoutY(startY);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), movingMedal);
        transition.setByX(endX - startX);
        transition.setByY(endY - startY);
        transition.setOnFinished(event -> {
            ((Pane) medal1.getParent()).getChildren().remove(movingMedal);
            animateProgressTransfer(1);
        });

        transition.play();
    }

    @FXML
    private ImageView levelUpImage;

    @FXML
    private HBox levelUpContainer;

    @FXML
    private VBox content;

    private boolean isAnimating = false;

    @FXML
    private HBox containerButton;

    public void showLevelUp(Pane root, int current, int total) {
        if (isAnimating) return;

        ShowFireworksLevelUp.showFireworks(root);

        final double originalX = levelUpImage.getLayoutX();
        final double originalY = levelUpImage.getLayoutY();

        // 🖼️ Thêm ảnh mới bên cạnh levelUpImage
        ImageView extraImage = new ImageView(new Image(getClass().getResource("/images/level-up.jpg").toExternalForm()));
        extraImage.setFitWidth(93);
        extraImage.setFitHeight(70);

        int levelUser = UserSession.getInstance().getLevel() + 1;

        Text levelUpText = new Text(Integer.toString(levelUser));
        levelUpText.setStyle("-fx-font-weight: bold; -fx-font-size: 40px; -fx-fill: #155588");

        VBox level = new VBox(levelUpText, extraImage);
        level.setAlignment(Pos.CENTER);
        level.setTranslateY(-12);
        level.setSpacing(7);
        level.setOpacity(0);
        levelUpContainer.getChildren().add(level);
        levelUpContainer.setSpacing(50);

        // Lấy tọa độ ảnh trong scene
        Bounds bounds = levelUpImage.localToScene(levelUpImage.getBoundsInLocal());
        double currentX = bounds.getMinX();
        double currentY = bounds.getMinY();

        // Tính tọa độ chính giữa màn hình
        double centerX = (root.getWidth() - levelUpImage.getFitWidth()) / 2;
        double centerY = (root.getHeight() - levelUpImage.getFitHeight()) / 2;

        // Di chuyển ảnh đến giữa màn hình
        TranslateTransition move = new TranslateTransition(Duration.seconds(1), levelUpContainer);
        move.setToX(centerX - currentX - 90);
        move.setToY(centerY - currentY);

        // Xoay ảnh
        RotateTransition rotate = new RotateTransition(Duration.seconds(1.5), levelUpImage);
        rotate.setByAngle(360);

        // Phóng to ảnh
        ScaleTransition scale = new ScaleTransition(Duration.seconds(1.5), levelUpImage);
        scale.setToX(2);
        scale.setToY(2);
        containerButton.setDisable(true);
        containerButton.setOpacity(1);

        ParallelTransition animation = new ParallelTransition(move, rotate, scale);

        levelUpContainer.toFront(); // Đưa cả StackPane lên trên
        content.setOpacity(0);

        animation.setOnFinished(x -> {
            isAnimating = false;

            FadeTransition fadeInLevel = new FadeTransition(Duration.seconds(0.7), level);
            fadeInLevel.setFromValue(0);
            fadeInLevel.setToValue(1);
            fadeInLevel.play();

            ScaleTransition scaleLevel = new ScaleTransition(Duration.seconds(0.7), level);
            scaleLevel.setToX(2);
            scaleLevel.setToY(2);
            scaleLevel.play();

            ScaleTransition scaleLevel2 = new ScaleTransition(Duration.seconds(0.4), levelUpText);
            scaleLevel2.setFromX(1);
            scaleLevel2.setFromY(1);
            scaleLevel2.setToX(1.3);
            scaleLevel2.setToY(1.3);
            scaleLevel2.setCycleCount(ScaleTransition.INDEFINITE);
            scaleLevel2.setAutoReverse(true); // Thu nhỏ lại sau khi phóng to
            scaleLevel2.play();

            root.setOnMouseClicked(event -> {
                if (isAnimating) return;

                ShowFireworksLevelUp.stopFireworks();
                containerButton.setDisable(false);
                containerButton.setOpacity(1);
                FadeTransition fadeOutLevel = new FadeTransition(Duration.seconds(0.2), level);
                fadeOutLevel.setFromValue(1);
                fadeOutLevel.setToValue(0);
                fadeOutLevel.setOnFinished(e -> levelUpContainer.getChildren().remove(level));

                fadeOutLevel.play();

                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), levelUpContainer);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);

                TranslateTransition moveBack = new TranslateTransition(Duration.seconds(0.5), levelUpContainer);
                moveBack.setToX(originalX - levelUpContainer.getLayoutX());
                moveBack.setToY(originalY - levelUpContainer.getLayoutY());

                ScaleTransition scaleBack = new ScaleTransition(Duration.seconds(0.5), levelUpImage);
                scaleBack.setToX(0.5);
                scaleBack.setToY(0.5);

                ParallelTransition resetAnimation = new ParallelTransition(fadeOut, moveBack, scaleBack);
                resetAnimation.setOnFinished(c -> {
                    levelUpContainer.setOpacity(1);
                    levelUpContainer.setTranslateX(0);
                    levelUpContainer.setTranslateY(0);
                    levelUpImage.setScaleX(1);
                    levelUpImage.setScaleY(1);
                    root.setOnMouseClicked(null);
                    content.setOpacity(1);

                    updateAfterLevelUp(current, total, levelUser);
                });

                resetAnimation.play();
            });
        });

        animation.play();
    }

    void updateAfterLevelUp(int current, int total, int level) {
        progressBar4.setProgress(1.0 * (current - total) / (Math.min(level * 4, 30)));

        progressLabel4.setText(String.format("%d/%d",0, Math.min(level * 4, 30)));
        String sql = "UPDATE players SET level = ? WHERE id = CAST(? AS UUID)";

        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, level);
            stmt.setObject(2, UUID.fromString(UserSession.getInstance().getUserId()));

            stmt.executeUpdate();

            System.out.println("Successfully updated level");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        animateProgressTransfer(current -  total);
    }
}