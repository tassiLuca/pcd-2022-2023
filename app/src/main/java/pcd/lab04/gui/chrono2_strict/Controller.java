package pcd.lab04.gui.chrono2_strict;

/**
 * Passive controller part, designed as a monitor.
 */
public class Controller {

    private static final int DELTA_TIME = 10;
    private final Flag stopFlag;
    private final Counter counter;
    private CounterView view;

    public Controller(Counter counter) {
        this.counter = counter;
        this.stopFlag = new Flag();
    }

    public synchronized void setView(CounterView view) {
        this.view = view;
    }

    public synchronized void notifyStarted() {
        CounterAgent agent = new CounterAgent(counter, stopFlag, view, DELTA_TIME);
        agent.start();
    }

    public synchronized void notifyStopped() {
        stopFlag.set();
    }

    public synchronized void notifyReset() {
        counter.reset();
        view.updateCountValue(counter.getValue());
    }
}
