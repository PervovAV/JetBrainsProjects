package tictactoe;

import java.util.Optional;

public class Robot {

    public static Optional<Coordinate> getMove(int[][] field, GameState.Player player) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                if (field[i][j] == 0) {
                    return Optional.of(new Coordinate(i, j));
                }
            }
        }
        return Optional.empty();
    }
}
