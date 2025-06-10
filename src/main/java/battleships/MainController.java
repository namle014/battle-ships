package battleships;

import client.NetworkManager;
import common.DailyQuestsSession;
import common.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

import static battleships.Main.pushScene;

public class MainController {
    private NetworkManager network;

    @FXML
    private GridPane gridBoard;

    @FXML
    private Button btnPlaySingle;

    @FXML
    private Button btnPlayOnline;

    @FXML
    private Button btnPlayWithFriends;

    @FXML
    private Button btnSettings;

    @FXML
    private HBox containerProgress1;

    @FXML
    public void initialize() throws IOException {
        ImageView settingsIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/setting.png")));
        settingsIcon.setFitWidth(57); // Kích thước icon
        settingsIcon.setFitHeight(57);
        containerProgress1.setMaxWidth(Region.USE_PREF_SIZE);
        containerProgress1.setMaxHeight(Region.USE_PREF_SIZE);
        btnSettings.setGraphic(settingsIcon); // Thêm icon vào button
        SoundManager.playBackgroundMusic("src/main/resources/sounds/battle-ship-short.mp3");
        updateInfoPersonal();
        updateDailyQuests();

        // set up client
        network = new NetworkManager();
        network.sendLogin(UserSession.getInstance().getUsername(), UserSession.getInstance().getLevel());
    }

    @FXML
    private void handlePlaySingle() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BoardOfflineView.fxml"));
        Parent modeView = loader.load();

        Stage stage = (Stage) btnPlaySingle.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }

    @FXML
    private void handlePlayOnline() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EndGameView.fxml"));
        Parent modeView = loader.load();
        ModuleLayer.Controller controller = (ModuleLayer.Controller) loader.getController();

        Stage stage = (Stage) btnPlaySingle.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }

    @FXML
    private void handleStartGame()  throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PWFModeView.fxml"));
        Parent modeView = loader.load();
        PWFModeController controller = loader.getController();
        controller.setNetwork(network);

        Stage stage = (Stage) btnPlaySingle.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }

    @FXML
    private void handleSetting()  throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SettingView.fxml"));
        Parent modeView = loader.load();

        Stage stage = (Stage) btnSettings.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }

    @FXML
    private ProgressBar progressBar4, progressBar3, progressBar2, progressBar1;
    @FXML
    private Label progressLabel1, progressLabel2, progressLabel3, progressLabel4;

    @FXML
    private Text winsText, totalPlaysText, accuracyText, levelText, nameText;

    public void updateInfoPersonal() {
        UserSession session = UserSession.getInstance();
        int wins = session.getTotalWins(), totalPlays = session.getTotalPlays();
        double accuracy = session.getTotalShots() != 0 ? (double) session.getTotalHits() / session.getTotalShots() : 0;
        String accuracyStr = String.format("%.1f", accuracy);
        int level = session.getLevel();
        int currentProgress = session.getCurrentProgressLevel();

        progressBar4.setProgress(1.0 * currentProgress / Math.min(level * 4, 30));
        progressLabel4.setText(String.format("%d/%d", currentProgress, Math.min(level * 4, 30)));
        levelText.setText(Integer.toString(level));
        nameText.setText(session.getUsername());
        winsText.setText(Integer.toString(wins));
        totalPlaysText.setText(Integer.toString(totalPlays));
        accuracyText.setText(accuracyStr);
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
}
