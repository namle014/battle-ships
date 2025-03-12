package database;

import common.UserSession;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String URL = "jdbc:postgresql://localhost:5432/battleship";
    private static final String USER = "postgres";
    private static final String PASSWORD = "26122004";

    // Kết nối database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Kiểm tra kết nối
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Kết nối PostgreSQL thành công!");
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }

    public static void updateSession(String username) {
        String query = "SELECT * FROM players WHERE username = ?";

        try (Connection conn = database.DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
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
            }
            else {
                System.out.println("Not Found!");
            }
        }
        catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
