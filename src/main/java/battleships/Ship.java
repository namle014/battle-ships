package battleships;

public class Ship {
    private static int counter = 0;
    private String id;
    private int startX, startY, size;
    private boolean horizontal;

    public Ship(int startX, int startY, int size, boolean horizontal) {
        this.startX = startX;
        this.startY = startY;
        this.size = size;
        this.horizontal = horizontal;
        this.id = "Ship-" + (++counter);
    }

    public String getId() {
        return id;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getSize() {
        return size;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setPosition(int x, int y) {
        this.startX = x;
        this.startY = y;
    }

    public void toggleOrientation() {
        this.horizontal = !this.horizontal;
    }

    public boolean isOccupied(int x, int y) {
        return horizontal ? (x >= startX && x < startX + size && y == startY)
                : (y >= startY && y < startY + size && x == startX);
    }
}
