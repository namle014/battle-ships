package common;

import java.util.ArrayList;
import java.util.List;

public class Network {
    // Yêu cầu đăng nhập
    public static class LoginRequest {
        public String username;
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
    }

    // Cập nhật phòng đợi
    public static class RoomUpdate {
        public List<PlayerInfo> players = new ArrayList<>();
        public boolean canStart; // True nếu đủ 2 người
    }

    // Tin nhắn chat
    public static class ChatMessage {
        public String sender;
        public String message;
    }
}