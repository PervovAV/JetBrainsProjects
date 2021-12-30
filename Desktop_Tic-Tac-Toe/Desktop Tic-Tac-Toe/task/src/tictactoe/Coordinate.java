package tictactoe;

public class Coordinate {
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }

    public int getY() { return y; }

    private int x;
    private int y;
}