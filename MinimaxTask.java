package checkers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

public class MinimaxTask extends RecursiveTask<Board> implements Serializable {

        final Board board;
        final int depth;
        final boolean top;
        final boolean max;
        final int id;

        MinimaxTask(Board board, int depth, boolean top) {
            this(board, depth, top, true, -1);
        }

        MinimaxTask(Board board, int depth, boolean top, boolean max, int id) {
            this.board = board;
            this.depth = depth;
            this.top = top;
            this.max = max;
            this.id = id;
        }

        @Override
        protected Board compute() {
            Board best = new Board(board, max ? Integer.MIN_VALUE : Integer.MAX_VALUE);
            if (board.top != 0 && board.bot != 0) {
                ArrayList<MinimaxTask> tasks = depth > 0 ? new ArrayList<MinimaxTask>() : null;
                for (Board move : Player.getPossibleMoves(board, max ? top : !top))
                    if (depth <= 0) {
                        int value = Board.value(move, top);
                        if (max ? (value >= best.score) : (value <= best.score)) best = new Board(move, value);
                    } else tasks.add(new MinimaxTask(move, depth - 1, top, !max, id));
                if (tasks != null) {
                    invokeAll(tasks);
                    for (MinimaxTask m : tasks) {
                        Board b = m.join();
                        if (max ? (b.score >= best.score) : (b.score <= best.score)) best = new Board(m.board, b.score);
                    }
                }
            }
            best.id = id;
            return best;
        }
    }