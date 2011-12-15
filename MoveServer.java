package checkers;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

public class MoveServer {
    
    static final ForkJoinPool pool = new ForkJoinPool();

    public static void main(String[] args) {
        if (args.length < 1) args = new String[]{"2597"};
        try {
            Socket socket = new ServerSocket(Integer.parseInt(args[0])).accept();
            final ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            for (;;) {
                System.out.println("Reading object...");
                final MinimaxTask m = (MinimaxTask) ois.readObject();
                System.out.println("Read task " + m.id);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("Computing");
                        pool.invoke(m);
                        Board b = m.join();
                        System.out.println("Writing " + b + " (" + b.id + ")");
                        try {
                            oos.writeObject(b);
                            oos.flush();
                        } catch (Exception e) {
                            System.err.println(e);
                            e.printStackTrace();
                            System.exit(1);
                        }
                        System.out.println("Done!");
                    }
                }).start();
            }
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
