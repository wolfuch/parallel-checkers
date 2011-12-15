package checkers;

import java.util.ArrayList;

public abstract class Player {

    final String name;

    Player(String name) {
        this.name = name;
    }

    abstract Board move(Board s, boolean top);

    static ArrayList<Board> getPossibleMoves(Board board, boolean top) {
        ArrayList<Board> jumps = new ArrayList<Board>();
        ArrayList<Board> nonjumps = new ArrayList<Board>();
        for (int piece = 0; piece < 32; piece++)
            if ((top && (board.top & (1 << piece)) != 0) ^ (!top && (board.bot & (1 << piece)) != 0)) {
                jumps.addAll(getJumps(board, piece, false, top));
                if (jumps.isEmpty()) nonjumps.addAll(getNonJumps(board, piece, top));
            }
        return jumps.isEmpty() ? nonjumps : jumps;
    }

    private static ArrayList<Board> getJumps(Board board, int piece, boolean addThis, boolean top) {
        ArrayList<Board> jumps = new ArrayList<Board>();
        int pieces = board.top | board.bot;
        int opp = top ? board.bot : board.top;
        int pos = piece % 8;
        if (piece < 24 && (top ^ ((1 << piece) & board.bot & board.kings) != 0)) {
            if (((1 << piece + 9) & pieces) == 0)
                if (((1 << piece + 4) & opp) != 0 && pos > 3 && pos < 7) jumps.addAll(getJumps(jump(board, piece, piece + 4, piece + 9, top), piece + 9, true, top));
                else if (((1 << piece + 5) & opp) != 0 && pos < 3) jumps.addAll(getJumps(jump(board, piece, piece + 5, piece + 9, top), piece + 9, true, top));
            if (((1 << piece + 7) & pieces) == 0)
                if (((1 << piece + 4) & opp) != 0 && pos > 0 && pos < 4) jumps.addAll(getJumps(jump(board, piece, piece + 4, piece + 7, top), piece + 7, true, top));
                else if (((1 << piece + 3) & opp) != 0 && pos > 4) jumps.addAll(getJumps(jump(board, piece, piece + 3, piece + 7, top), piece + 7, true, top));
        }
        if (piece > 7 && (!top ^ ((1 << piece) & board.top & board.kings) != 0)) {
            if (((1 << piece - 9) & pieces) == 0)
                if (((1 << piece - 4) & opp) != 0 && pos > 0 && pos < 4) jumps.addAll(getJumps(jump(board, piece, piece - 4, piece - 9, top), piece - 9, true, top));
                else if (((1 << piece - 5) & opp) != 0 && pos > 4) jumps.addAll(getJumps(jump(board, piece, piece - 5, piece - 9, top), piece - 9, true, top));
            if (((1 << piece - 7) & pieces) == 0)
                if (((1 << piece - 4) & opp) != 0 && pos > 3 && pos < 7) jumps.addAll(getJumps(jump(board, piece, piece - 4, piece - 7, top), piece - 7, true, top));
                else if (((1 << piece - 3) & opp) != 0 && pos < 3) jumps.addAll(getJumps(jump(board, piece, piece - 3, piece - 7, top), piece - 7, true, top));
        }
        if (addThis && jumps.isEmpty()) jumps.add(board);
        return jumps;
    }

    private static Board jump(Board board, int piece, int jumpedPiece, int newPiece, boolean top) {
        int newBot = !top ? (board.bot | (1 << newPiece)) & ~(1 << piece) : board.bot & ~(1 << jumpedPiece);
        int newTop = top ? (board.top | (1 << newPiece)) & ~(1 << piece) : board.top & ~(1 << jumpedPiece);
        int newKings = board.kings;
        if ((newKings & (1 << piece)) != 0) newKings = (newKings | (1 << newPiece)) & ~(1 << piece);
        if ((newKings & (1 << jumpedPiece)) != 0) newKings = (newKings & ~(1 << jumpedPiece));
        if ((top && (((1 << piece) & board.top & board.kings) != 0 || newPiece >= 28))
                ^ (!top && (((1 << piece) & board.bot & board.kings) != 0 || newPiece <= 3)))
            newKings = ((newKings | (1 << newPiece)) & ~(1 << piece));
        assert ((newBot & newTop) == 0);
        if (newKings != 0) assert ((newBot & newKings) != 0 || (newTop & newKings) != 0);
        return new Board(newBot, newTop, newKings);
    }

    private static ArrayList<Board> getNonJumps(Board board, int piece, boolean top) {
        ArrayList<Board> nonjumps = new ArrayList<Board>();
        int pieces = board.top | board.bot;
        int pos = piece % 8;
        if (piece < 28 && (top ^ ((1 << piece) & board.bot & board.kings) != 0)) { // if piece isn't on king row and is either top player xor has a king
            if (((1 << piece + 4) & pieces) == 0) nonjumps.add(shift(board, piece, piece + 4, top)); // if one diagonal is unoccupied, go there
            if (((1 << piece + 5) & pieces) == 0 && pos < 3) nonjumps.add(shift(board, piece, piece + 5, top)); // depending on the row, go to the other as well
            else if (((1 << piece + 3) & pieces) == 0 && pos > 4) nonjumps.add(shift(board, piece, piece + 3, top)); // unless it's on one of the walls
        }
        if (piece > 3 && (!top ^ ((1 << piece) & board.top & board.kings) != 0)) {
            if (((1 << piece - 4) & pieces) == 0) nonjumps.add(shift(board, piece, piece - 4, top));
            if (((1 << piece - 5) & pieces) == 0 && pos > 4) nonjumps.add(shift(board, piece, piece - 5, top));
            else if (((1 << piece - 3) & pieces) == 0 && pos < 3) nonjumps.add(shift(board, piece, piece - 3, top));
        }
        return nonjumps;
    }

    private static Board shift(Board board, int piece, int newPiece, boolean top) {
        int newBot = !top ? (board.bot | (1 << newPiece)) & ~(1 << piece) : board.bot;
        int newTop = top ? (board.top | (1 << newPiece)) & ~(1 << piece) : board.top;
        int newKings = (top && (((1 << piece) & board.top & board.kings) != 0 || newPiece >= 28))
                ^ (!top && (((1 << piece) & board.bot & board.kings) != 0 || newPiece <= 3))
                ? (board.kings | (1 << newPiece)) & ~(1 << piece) : board.kings;
        assert ((newBot & newTop) == 0);
        if (newKings != 0) assert ((newBot & newKings) != 0 || (newTop & newKings) != 0);
        return new Board(newBot, newTop, newKings);
    }
}