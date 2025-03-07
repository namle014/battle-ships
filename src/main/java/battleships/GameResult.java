package battleships;

public class GameResult {
    private int shipsDestroyed;
    private int hits;
    private int misses;
    private int accuracy;
    private int bestStreak;

    public GameResult(int shipsDestroyed, int hits, int misses, int accuracy, int bestStreak) {
        this.shipsDestroyed = shipsDestroyed;
        this.hits = hits;
        this.misses = misses;
        this.accuracy = accuracy;
        this.bestStreak = bestStreak;
    }

    public int getShipsDestroyed() { return shipsDestroyed; }
    public int getHits() { return hits; }
    public int getMisses() { return misses; }
    public int getAccuracy() { return accuracy; }
    public int getBestStreak() { return bestStreak; }
}
