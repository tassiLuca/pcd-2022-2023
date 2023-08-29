package pcd.lab02.lost_updates;

public class TestCounter {

    private static final int N_TIMES = 1_000_000;

    public static void main(String[] args) throws Exception {
        // int ntimes = Integer.parseInt(args[0]);
        final UnsafeCounter counter = new UnsafeCounter(0);
        final Worker w1 = new Worker(counter, N_TIMES);
        final Worker w2 = new Worker(counter, N_TIMES);
        final Chrono chrono = new Chrono();
        chrono.start();
        w1.start();
        w2.start();
        w1.join();
        w2.join();
        chrono.stop();
        System.out.println("Counter final value: " + counter.getValue() + " in " + chrono.getTime() + "ms.");
    }
}
