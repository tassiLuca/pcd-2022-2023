package pcd.lab03.liveness.observer;

import java.util.ArrayList;
import java.util.List;

class ObservedImpl implements Observed {

    private final List<Observer> observers = new ArrayList<>();
    private int state;

    @Override
    public synchronized void register(Observer obs) {
        observers.add(obs);
    }

    @Override
    public synchronized void update() {
        state++;
        notifyChange();
    }

    @Override
    public synchronized int getState() {
        return state;
    }

    private void notifyChange() {
        for (Observer o: observers) {
            o.notifyStateChanged(this);
        }
    }
}
