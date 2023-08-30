package pcd.lab04.monitors.latch;

/*
 * Latch - to be implemented
 */
public class LatchImpl implements Latch {

    private int count;

    public LatchImpl(final int count) {
        this.count = count;
    }

    @Override
    public synchronized void await() throws InterruptedException {
        while (count > 0) {
            wait();
        }
    }

    @Override
    public synchronized void countDown() {
        count = count - 1;
        if (count == 0) {
            notifyAll();
        }
    }
}
