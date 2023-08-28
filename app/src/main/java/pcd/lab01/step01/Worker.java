package pcd.lab01.step01;

/**
 * Base class for very simple agent structure.
 */
public abstract class Worker extends Thread {

    public Worker(final String name) {
        super(name);
    }

    protected void sleepFor(final long ms) {
        try {
            sleep(ms);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    protected void print(final String msg) {
        synchronized (System.out) {
            System.out.print(msg);
        }
    }

    protected void println(final String msg) {
        synchronized (System.out) {
            System.out.println(msg);
        }
    }
}
