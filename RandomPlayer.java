package checkers;

import java.util.ArrayList;
import java.util.Random;

public class RandomPlayer extends Player {

    final ThreadLocal<Random> random = new ThreadLocal<Random>();

    RandomPlayer(String name) {
        super(name);
        random.set(new Random());
    }

    @Override
    Board move(Board board, boolean top) {
        ArrayList<Board> moves = getPossibleMoves(board, top);
        return (moves.isEmpty()) ? board : moves.get(random.get().nextInt(moves.size()));
    }
}
