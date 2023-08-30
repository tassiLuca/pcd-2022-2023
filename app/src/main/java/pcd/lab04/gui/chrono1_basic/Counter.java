package pcd.lab04.gui.chrono1_basic;

import java.util.*;

public class Counter {

    private final ArrayList<CounterEventListener> listeners;
    private int cont;
    private final int base;

    public Counter(int base) {
        this.cont = base;
        this.base = base;
        listeners = new ArrayList<>();
    }

    public synchronized void inc() {
        cont++;
        System.out.println("count "+cont);
        notifyEvent(new CounterEvent(cont));
    }

    public synchronized void reset() {
        cont = base;
        notifyEvent(new CounterEvent(cont));
    }

    public synchronized int getValue(){
        return cont;
    }

    public synchronized void addListener(CounterEventListener l){
        listeners.add(l);
    }

    private void notifyEvent(CounterEvent ev) {
        for (CounterEventListener l: listeners){
            l.counterChanged(ev);   // WARNING: calling "alien" method inside a synchronized block!!!!!
        }
    }
}
