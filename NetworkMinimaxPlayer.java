package checkers;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class NetworkMinimaxPlayer extends ParallelMinimaxPlayer {

    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    Random rng = new Random();

    NetworkMinimaxPlayer(String name, int difficulty, String host, int port) {
        super(name, difficulty);
        try {
            Socket socket = new Socket(host, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    Board move(Board board, boolean top) {
        NetworkMinimaxTask m = new NetworkMinimaxTask(board, difficulty, top);
        pool.invoke(m);
        return m.join();
    }

    private class NetworkMinimaxTask extends MinimaxTask {

        NetworkMinimaxTask(Board board, int depth, boolean top) {
            super(board, depth, top);
        }

        @Override
        protected Board compute() {
            Board best = new Board(board, max ? Integer.MIN_VALUE : Integer.MAX_VALUE);
            if (board.top != 0 && board.bot != 0) {
                ArrayList<MinimaxTask> tasks = null;
                ArrayList<Board> sent = null;
                for (Board move : getPossibleMoves(board, max ? top : !top))
                    if (difficulty <= 0) {
                        int value = Board.value(move, top);
                        if (max ? (value >= best.score) : (value <= best.score)) best = new Board(move, value);
                    } else {
                        if (tasks == null || rng.nextBoolean()) {
                            if (tasks == null) tasks = new ArrayList<MinimaxTask>();
                            MinimaxTask task = new MinimaxTask(move, difficulty - 1, top, !max, -1);
                            tasks.add(task);
                        } else {
                           
                            if (sent == null) sent = new ArrayList<Board>();
                            move.id = sent.size();
                            sent.add(move);
                             System.out.println("Sending board " + move + " with id " + move.id);
                            MinimaxTask task = new MinimaxTask(move, difficulty - 1, top, !max, move.id);
                            try {
                                oos.writeObject(task);
                            } catch (Exception e) {
                                System.err.println(e);
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }
                    }
                if (tasks != null) {
                    invokeAll(tasks);
                    for (MinimaxTask m : tasks) {
                        Board b = m.join();
                        if (max ? (b.score >= best.score) : (b.score <= best.score)) best = new Board(m.board, b.score);
                    }
                }
                if (sent != null)
                    for (int i = 0; i < sent.size(); i++) {
                        Board b = null;
                        try {
                            b = (Board) ois.readObject();
                            System.out.println("b's id is " + b.id);
                        } catch (Exception e) {
                            System.err.println(e);
                            e.printStackTrace();
                            System.exit(1);
                        }
                        if (b != null && (max ? (b.score >= best.score) : (b.score <= best.score))) best = new Board(sent.get(b.id), b.score);
                    }
            }
            return best;
        }
    }
}
