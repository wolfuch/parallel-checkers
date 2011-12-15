package checkers;

public class MinimaxPlayer extends Player {

    final int difficulty;

    MinimaxPlayer(String name, int difficulty) {
        super(name);
        this.difficulty = difficulty;
    }

    @Override
    Board move(Board board, boolean top) {
        return maxMove(board, difficulty, top);
    }

    private Board maxMove(Board board, int depth, boolean top) {
        Board best = new Board(board, Integer.MIN_VALUE);
        if (board.top != 0 && board.bot != 0)
            for (Board move : getPossibleMoves(board, top)) {
                Board newBoard = depth == 0 ? new Board(move, Board.value(move, top)) : minMove(move, depth - 1, top);
                if (newBoard.score >= best.score) best = new Board(move, newBoard.score);
            }
        return best;
    }

    private Board minMove(Board board, int depth, boolean top) {
        Board best = new Board(board, Integer.MAX_VALUE);
        if (board.top != 0 && board.bot != 0)
            for (Board move : getPossibleMoves(board, !top)) {
                Board newBoard = depth == 0 ? new Board(move, Board.value(move, top)) : maxMove(move, depth - 1, top);
                if (newBoard.score <= best.score) best = new Board(move, newBoard.score);
            }
        return best;
    }
}
