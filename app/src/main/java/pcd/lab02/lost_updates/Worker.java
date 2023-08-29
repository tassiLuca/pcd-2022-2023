package pcd.lab02.lost_updates;


public class Worker extends Thread {

    private final UnsafeCounter counter;
    private final int ntimes;

    public Worker(UnsafeCounter counter, int ntimes) {
        this.counter = counter;
        this.ntimes = ntimes;
    }

    public void run() {
        for (int i = 0; i < ntimes; i++) {
            counter.inc();
        }
    }
}
