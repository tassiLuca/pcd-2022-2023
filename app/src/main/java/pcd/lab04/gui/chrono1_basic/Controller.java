package pcd.lab04.gui.chrono1_basic;

public class Controller {

    private static final int DELTA_TIME = 10;
    private final Flag stopFlag;
    private final Counter counter;

    public Controller(Counter counter) {
        this.counter = counter;
        this.stopFlag = new Flag();
    }

    public void notifyStarted() {
        CounterAgent agent = new CounterAgent(counter, stopFlag, DELTA_TIME);
        agent.start();
    }

    public void notifyStopped() {
        stopFlag.set();
    }

    public void notifyReset() {
        counter.reset();
        stopFlag.reset();
    }
}
