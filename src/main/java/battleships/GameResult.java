package battleships;

public class GameResult {
    private int shipsDestroyed;
    private int hits;
    private int misses;
    private int accuracy;
    private int bestStreak;
    private int score;
    private boolean firstHit;
    private String playerName;

    public GameResult(int shipsDestroyed, int hits, int misses, int accuracy, int bestStreak,int score) {
        this.shipsDestroyed = shipsDestroyed;
        this.hits = hits;
        this.misses = misses;
        this.accuracy = accuracy;
        this.bestStreak = bestStreak;
        this.score=score;
    }

    public GameResult() {}

    public int getShipsDestroyed() { return shipsDestroyed; }
    public int getHits() { return hits; }
    public int getMisses() { return misses; }
    public int getAccuracy() { return accuracy; }
    public int getBestStreak() { return bestStreak; }
    public int getScore() { return score; }
    public boolean isFirstHit() { return firstHit; }
    public String getPlayerName() { return playerName; }

    public void setShipsDestroyed(int shipsDestroyed) {
        this.shipsDestroyed = shipsDestroyed;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }
    public void setMisses(int misses) {
        this.misses = misses;
    }
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }
    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void setFirstHit(boolean firstHit) {
        this.firstHit = firstHit;
    }
    public void setPlayerName(String playerName) {this.playerName = playerName;}
}
