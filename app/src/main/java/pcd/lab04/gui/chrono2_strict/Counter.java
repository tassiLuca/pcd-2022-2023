package pcd.lab04.gui.chrono2_strict;

/**
 * Model designed as a monitor.
 */
public class Counter {

    private int cont;
    private final int base;

    public Counter(int base) {
        this.cont = base;
        this.base = base;
    }

    public synchronized void inc() {
        cont++;
        System.out.println("count "+cont);
    }

    public synchronized void reset(){
        cont = base;
    }

    public synchronized int getValue(){
        return cont;
    }
}
