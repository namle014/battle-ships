package common;

import battleships.GameResult;
import battleships.Ship;

import java.util.ArrayList;
import java.util.List;

public class Network {
    // Yêu cầu đăng nhập
    public static class LoginRequest {
        public String username;
        public int level;
    }

    // Phản hồi đăng nhập
    public static class LoginResponse {
        public boolean success;
        public String message;
        public PlayerInfo playerInfo;
    }

    // Thông tin người chơi
    public static class PlayerInfo {
        public String username;
        public int level;
        public boolean isReady;
    }

    // Yêu cầu tạo phòng
    public static class CreateRoomRequest {}

    // Phản hồi tạo phòng
    public static class RoomCreatedResponse {
        public String roomId;
    }

    // Yêu cầu tham gia phòng
    public static class JoinRoomRequest {
        public String roomId;
    }

    // Yêu cầu danh sách phòng
    public static class RoomListRequest {}

    // Yêu cầu thoát phòng
    public static class LeaveRoomRequest {}

    // Danh sách phòng
    public static class RoomListResponse {
        public List<RoomInfo> rooms = new ArrayList<>();
    }

    // Thông tin phòng
    public static class RoomInfo {
        public String roomId;
        public List<PlayerInfo> players = new ArrayList<>();
        public List<Ship> Player1ships = new ArrayList<>();
        public List<Ship> Player2ships = new ArrayList<>();
    }

    // Cập nhật phòng đợi
    public static class RoomUpdate {
        public List<PlayerInfo> players = new ArrayList<>();
        public boolean canStart; // True nếu đủ 2 người
        public PlayerInfo opponentInfo;
    }

    // Tin nhắn chat
    public static class ChatMessage {
        public String sender;
        public String message;
    }

    // Yêu cầu ready
    public static class ReadyRequest {
        public boolean isReady;
        public List<Ship> ships = new ArrayList<>();
    }

    public static class ReadyResponse {
        public boolean startGame;
        public boolean yourTurn;
        public List<Ship> enemyShips = new ArrayList<>();
    }

    public static class AttackRequest {
        public int col;
        public int row;
        public boolean success;
        public boolean endGame;
        public GameResult result;
    }

    public static class AttackResponse {
        public int col;
        public int row;
        public boolean success;
        public boolean endGame;
        public GameResult result;
    }
}