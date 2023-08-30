package pcd.lab04.gui2_mvc_unresponsive;

import java.util.ArrayList;
import java.util.List;

public class MyModel {

    private final List<ModelObserver> observers;
    private int state;

    public MyModel() {
        state = 0;
        observers = new ArrayList<>();
    }

    public void update() {
        state++;
        notifyObservers();
    }

    public int getState() {
        return state;
    }

    public void addObserver(final ModelObserver obs) {
        observers.add(obs);
    }

    private void notifyObservers() {
        for (ModelObserver obs: observers){
            obs.modelUpdated(this);
        }
    }
}
