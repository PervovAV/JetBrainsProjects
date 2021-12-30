package tictactoe;

enum PlayerType {
    HUMAN, ROBOT;
}

public class GameSettings {
    private PlayerType x_player = PlayerType.HUMAN;
    private PlayerType y_player = PlayerType.HUMAN;

    public void setXPlayer(String type) {
        if ("Robot".equals(type)) {
            x_player = PlayerType.ROBOT;
        }
    }

    public void setYPlayer(String type) {
        if ("Robot".equals(type)) {
            y_player = PlayerType.ROBOT;
        }
    }

    public PlayerType getXPlayer() {
        return x_player;
    }

    public PlayerType getYPlayer() {
        return y_player;
    }
}
