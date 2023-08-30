package pcd.lab04.monitors.barrier;

import java.util.Random;

public class Worker extends Thread {

    private final Barrier barrier;

    public Worker(final String name, final Barrier barrier) {
        super(name);
        this.barrier = barrier;
    }

    public void run() {
        Random gen = new Random(System.nanoTime());
        try {
            waitFor(gen.nextInt(3000));
            log("before");
            barrier.hitAndWaitAll();
            log("after");
        } catch (InterruptedException ex) {
            log("Interrupted!");
        }
    }

    private void log(final String msg) {
        synchronized(System.out) {
            System.out.println("[ "+getName()+" ] "+msg);
        }
    }

    private void waitFor(final long ms) throws InterruptedException {
        Thread.sleep(ms);
    }
}
