package pcd.lab04.gui.chrono2_strict;

/**
 * View designed as a monitor.
 */
public class CounterView {

    private final CounterGUI gui;

    public CounterView(Controller contr, int initialValue){
        gui = new CounterGUI(contr,initialValue);
    }

    public synchronized void setCountingState() {
        gui.setCountingState();
    }

    public synchronized void setIdleState() {
        gui.setIdleState();
    }

    public synchronized void updateCountValue(int value) {
        gui.updateCountValue(value);
    }

    public synchronized void display() {
        gui.display();
    }
}
