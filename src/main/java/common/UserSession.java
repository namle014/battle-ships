package common;

public class UserSession {
    private static UserSession instance; // Singleton instance

    private String userId;
    private String username;
    private int level;
    private int totalPlays;
    private int totalWins;
    private int totalHits;
    private int totalShots;
    private int musicVolume;
    private int soundVolume;
    private String password;
    private int currentProgressLevel;

    private UserSession(String userId, String username, int level, int totalPlays, int totalWins,
                        int totalHits, int totalShots, int musicVolume, int soundVolume, String password,
                        int currentProgressLevel) {
        this.userId = userId;
        this.username = username;
        this.level = level;
        this.totalPlays = totalPlays;
        this.totalWins = totalWins;
        this.totalHits = totalHits;
        this.totalShots = totalShots;
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
        this.password = password;
        this.currentProgressLevel = currentProgressLevel;
    }

    // Trả về instance duy nhất của UserSession
    public static UserSession getInstance() {
        return instance;
    }

    // Hàm đăng nhập, tạo session mới
    public static void login(String userId, String username, int level, int totalPlays, int totalWins,
                             int totalShots, int totalHits, int musicVolume, int soundVolume, String password,
                             int currentProgressLevel) {
        instance = new UserSession(userId, username, level, totalPlays, totalWins, totalHits, totalShots,
                        musicVolume, soundVolume, password,  currentProgressLevel);
    }

    // Hàm đăng xuất
    public static void logout() {
        instance = null;
    }

    // Getter để lấy thông tin người dùng
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public int getLevel() { return level; }
    public int getTotalPlays() { return totalPlays; }
    public int getTotalWins() { return totalWins; }
    public int getTotalHits() { return totalHits; }
    public int getTotalShots() { return totalShots; }
    public int getMusicVolume() { return musicVolume; }
    public int getSoundVolume() { return soundVolume; }
    public String getPassword() { return password; }
    public int getCurrentProgressLevel() { return currentProgressLevel; }

    @Override
    public String toString() {
        return "UserSession{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", level=" + level +
                ", totalPlays=" + totalPlays +
                ", totalWins=" + totalWins +
                '}';
    }
}
