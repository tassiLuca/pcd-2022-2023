package pcd.lab04.gui.chrono2_strict;

/**
 * Active component doing counting.
 */
public class CounterAgent extends Thread {

    private final Counter counter;
    private final Flag stopFlag;
    private final long delta;
    private final CounterView view;

    public CounterAgent(Counter c, Flag stopFlag, CounterView view, long delta) {
        counter = c;
        this.stopFlag = stopFlag;
        this.delta = delta;
        this.view = view;
    }

    public void run() {
        stopFlag.reset();
        view.setCountingState();
        while (!stopFlag.isSet()) {
            counter.inc();
            view.updateCountValue(counter.getValue());
            System.out.println(counter.getValue());
            try {
                Thread.sleep(delta);
            } catch(Exception ignored){ }
        }
    }
}
