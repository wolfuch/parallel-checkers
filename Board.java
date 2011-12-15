package checkers;

import java.io.Serializable;

public class Board implements Serializable {

    final int bot, top, kings, score;
    int id = -1;

    Board(int bot, int top, int kings) {
        this.bot = bot; // black
        this.top = top; // red
        this.kings = kings;
        this.score = 0;
    }

    Board(Board board, int score) {
        this.bot = board.bot; // black
        this.top = board.top; // red
        this.kings = board.kings;
        this.score = score;
        id = board.id;
    }
    
    Board(Board board, int score, int id) {
        this.bot = board.bot; // black
        this.top = board.top; // red
        this.kings = board.kings;
        this.score = score;
        this.id = id;
    }

    Board() {
        this(0xFFF00000, 0x00000FFF, 0);
    }

    @Override
    public String toString() {
        return padZeros(this.bot) + " " + padZeros(this.top) + " " + padZeros(this.kings);
    }

    static String padZeros(int i) {
        String r = Integer.toBinaryString(i);
        while (r.length() < 32) r = "0" + r;
        return r;
    }

    public static int value(Board board, boolean top) {
        if ((top && board.bot == 0) ^ (!top && board.top == 0)) return Integer.MAX_VALUE;
        if ((top && board.top == 0) ^ (!top && board.bot == 0)) return Integer.MIN_VALUE;
        return (top ? 1 : -1) * ((Integer.bitCount(board.top) + Integer.bitCount(board.top & board.kings)) - (Integer.bitCount(board.bot) + Integer.bitCount(board.bot & board.kings)));
    }
}
