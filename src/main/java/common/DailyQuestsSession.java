package common;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DailyQuestsSession {
    private static List<DailyQuestsSession> instances = new ArrayList<>(); // Danh sách nhiệm vụ

    private int id;
    private String playerId;
    private int questId;
    private int currentProgress;
    private boolean completed;
    private Date lastReset;

    public DailyQuestsSession(int id, String playerId, int questId, int currentProgress, boolean completed, Date lastReset) {
        this.id = id;
        this.playerId = playerId;
        this.questId = questId;
        this.currentProgress = currentProgress;
        this.completed = completed;
        this.lastReset = lastReset;
    }

    // Hàm lưu danh sách nhiệm vụ hàng ngày
    public static void setDailyQuests(List<DailyQuestsSession> quests) {
        instances = quests;
    }

    // Lấy danh sách nhiệm vụ hàng ngày
    public static List<DailyQuestsSession> getDailyQuests() {
        return instances;
    }

    public static void logout() {
        instances = null;
    }

    public int getId() { return this.id; }
    public String getPlayerId() { return this.playerId; }
    public int getQuestId() { return this.questId; }
    public int getCurrentProgress() { return this.currentProgress; }
    public boolean isCompleted() { return this.completed; }
    public Date getLastReset() { return this.lastReset; }
}
