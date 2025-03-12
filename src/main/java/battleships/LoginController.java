package battleships;

import common.DailyQuestsSession;
import common.UserSession;
import database.DatabaseHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static battleships.Main.pushScene;

public class LoginController  {
    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private void handleLogin() throws IOException {
        String user = username.getText();
        String pass = password.getText();

        if (login(user,pass)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent modeView = loader.load();

            Stage stage = (Stage) btnSignUp.getScene().getWindow();

            Scene currentScene = stage.getScene();
            pushScene(currentScene);

            stage.setScene(new Scene(modeView));

            stage.show();
        }
        else {
            System.out.println("Wrong username or password");
        }
    }

    @FXML
    private Button btnSignUp;

    @FXML
    private BorderPane root;
    @FXML
    public void initialize() {
        Platform.runLater(() -> root.requestFocus());

        root.setOnMouseClicked(event -> {
            if (!(password.isFocused() || username.isFocused())) {
                return;
            }
            root.requestFocus();
        });
    }

    @FXML
    private void handleSignUp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUpView.fxml"));
        Parent modeView = loader.load();

        Stage stage = (Stage) btnSignUp.getScene().getWindow();

        Scene currentScene = stage.getScene();
        pushScene(currentScene);

        stage.setScene(new Scene(modeView));

        stage.show();
    }

    public static boolean login(String username, String password) {
        String query = "SELECT * FROM players WHERE username = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String passwordHash = rs.getString("password");

                if (BCrypt.checkpw(password, passwordHash)) {
                    UserSession.login(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getInt("level"),
                            rs.getInt("total_plays"),
                            rs.getInt("total_wins"),
                            rs.getInt("total_shots"),
                            rs.getInt("total_hits"),
                            rs.getInt("music_volume"),
                            rs.getInt("sound_volume"),
                            rs.getString("password"),
                            rs.getInt("current_progress_level")
                    );

                    // Tiếp tục lấy thông tin nhiệm vụ hàng ngày
                    String playerId = rs.getString("id");
                    getPlayerDailyQuests(playerId);

                    return true;
                } else {
                    System.out.println("Tên tài khoản hoặc mật khẩu không đúng");
                }
            } else {
                System.out.println("Tên tài khoản hoặc mật khẩu không đúng");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public static void getPlayerDailyQuests(String playerId) {
        String query = "SELECT * FROM player_daily_quests WHERE player_id = CAST(? AS UUID)";

        List<DailyQuestsSession> quests = new ArrayList<>();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, UUID.fromString(playerId));  // Chuyển thành UUID trước khi truyền vào
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {  // Lặp để lấy tất cả nhiệm vụ
                quests.add(new DailyQuestsSession(
                        rs.getInt("id"),
                        rs.getString("player_id"),
                        rs.getInt("quest_id"),
                        rs.getInt("current_progress"),
                        rs.getBoolean("completed"),
                        rs.getDate("last_reset")
                ));
            }

            quests.sort(Comparator.comparingInt(DailyQuestsSession::getQuestId));

            DailyQuestsSession.setDailyQuests(quests);

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy nhiệm vụ hàng ngày: " + e.getMessage());
        }
    }

}
