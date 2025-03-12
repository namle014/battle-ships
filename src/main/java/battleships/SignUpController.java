package battleships;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static battleships.Main.popScene;
import static battleships.Main.pushScene;

public class SignUpController {
    @FXML
    private PasswordField password, confirmPassword;

    @FXML
    private TextField username;

    @FXML
    private void handleSignUp() throws IOException {
        String pass = password.getText();
        String passConfirm = confirmPassword.getText();
        String user = username.getText();

        if (pass.isEmpty() || passConfirm.isEmpty() || user.isEmpty()) {
            System.out.println("Please fill all the fields");
            return;
        }

        if (pass.equals(passConfirm)) {
            if (registerUser(user, pass)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
                Parent modeView = loader.load();

                Stage stage = (Stage) btnSignUp.getScene().getWindow();

                Scene currentScene = stage.getScene();
                pushScene(currentScene);

                stage.setScene(new Scene(modeView));

                stage.show();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Passwords do not match");
            alert.showAndWait();
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
            if (!(password.isFocused() || username.isFocused() || confirmPassword.isFocused())) {
                return;
            }
            root.requestFocus();
        });
    }

    public static boolean registerUser(String username, String plainPassword) {
        // Kiểm tra xem username đã tồn tại chưa
        if (isUsernameExists(username)) {
            System.out.println("Tên người dùng đã tồn tại!");
            return false;
        }

        // Hash mật khẩu
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));

        // Câu lệnh INSERT
        String query = "INSERT INTO players (id, username, password) VALUES (gen_random_uuid(), ?, ?)";

        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
            System.out.println("Đăng ký thành công!");
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi đăng ký: " + e.getMessage());
            return false;
        }
    }

    // Hàm kiểm tra username có tồn tại hay chưa
    private static boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM players WHERE username = ?";

        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra username: " + e.getMessage());
        }
        return false;
    }

    @FXML
    private void handleBack() {
        Scene previousScene = popScene();
        if (previousScene != null) {
            Stage stage = (Stage) btnSignUp.getScene().getWindow();
            stage.setScene(previousScene);
        }
    }
}
