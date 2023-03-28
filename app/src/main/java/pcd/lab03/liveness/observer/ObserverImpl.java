package pcd.lab03.liveness.observer;

import java.util.ArrayList;
import java.util.List;

class ObserverImpl implements Observer {

    private final List<Observed> observed = new ArrayList<>();

    @Override
    public synchronized void observe(Observed obj) {
        observed.add(obj);
        obj.register(this);
    }

    @Override
    public synchronized void notifyStateChanged(Observed obs) {
        synchronized(System.out) {
            System.out.println("state changed: " + obs.getState());
        }
    }

    @Override
    public synchronized int getOverallState() {
        int sum = 0;
        for (Observed o: observed) {
            sum += o.getState();
        }
        return sum;
    }
}
