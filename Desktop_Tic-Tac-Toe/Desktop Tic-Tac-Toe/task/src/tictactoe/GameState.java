package tictactoe;

import java.util.ArrayList;
import java.util.Optional;

class GameState {

    public enum Player {
        X(1), O(2);

        private int value;

        Player(int x) {
            value = x;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Status {
        BEFORE, IN_PROGRESS, FINISHED
    }

    public GameState(int side) {
        turn = Player.X;
        this.side = side;
        field = new int[side][side];
        status = Status.BEFORE;
    }

    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
        System.out.println("X: " + gameSettings.getXPlayer().toString());
        System.out.println("Y: " + gameSettings.getYPlayer().toString());
    }

    public Status getStatus() { return status; }

    public boolean isGameOver() { return status == Status.FINISHED; }

    public boolean isGameBefore() { return status == Status.BEFORE; }

    public Optional<Player> getWinner() { return winner; }

    public int[][] getField() {
        return field;
    }

    public Player getTurn() {
        return turn;
    }

    public void setStatus(Status status) {
        this.status = status;
    }



    public Optional<Player> makeMove(Coordinate coord) {
        int x = coord.getX();
        int y = coord.getY();
        System.out.println(x + " " + y);

        if (status == Status.FINISHED || field[x][y] != 0) {
            return Optional.empty();
        }

        field[x][y] = turn.value;
        Player prevTurn = turn;
        turn = turn == Player.X ? Player.O : Player.X;

        updateStatus();
        return Optional.of(prevTurn);
    }

    private Optional<Player> toPlayer(int x) {
        return x == 1 ? Optional.of(Player.X)
            : (x == 2 ? Optional.of(Player.O) : Optional.empty());
    }

    private void updateStatus() {
        status = Status.IN_PROGRESS;

        if (isFieldFull()) {
            winner = Optional.empty();
            status = Status.FINISHED;
        }

        var results = new ArrayList<Optional<Player>>();
        results.add(iterate(0, 0, 0, 1));
        results.add(iterate(1, 0, 0, 1));
        results.add(iterate(2, 0, 0, 1));
        results.add(iterate(0, 0, 1, 0));
        results.add(iterate(0, 1, 1, 0));
        results.add(iterate(0, 2, 1, 0));
        results.add(iterate(0, 0, 1, 1));
        results.add(iterate(2, 0, -1, 1));

        for (var res : results) {
            if (res.isPresent()) {
                winner = res;
                status = Status.FINISHED;
            }
        }
    }

    private Optional<Player> iterate(int xFrom, int yFrom, int dx, int dy) {
        int v0 = field[xFrom][yFrom];
        if (v0 == 0) {
            return Optional.empty();
        }
        for (int i = 1; i < side; i++) {
            int x = xFrom + i * dx;
            int y = yFrom + i * dy;
            if (field[x][y] != v0) {
                return Optional.empty();
            }
        }
        return toPlayer(v0);
    }

    private boolean isFieldFull() {
        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                if (field[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private int side;
    private int[][] field;
    private Player turn;
    private Optional<Player> winner = Optional.empty();
    private Status status;
    private GameSettings gameSettings;
}