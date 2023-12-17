package pcd.lab04.monitors.sync_cell;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;

public class SyncCell2 {

    private int value;
    private boolean available;
    private final Lock mutex;
    private final Condition isAvail;

    public SyncCell2() {
        available = false;
        mutex = new ReentrantLock();
        isAvail = mutex.newCondition();
    }

    public void set(int v) {
        try {
            mutex.lock();
            value = v;
            available = true;
            isAvail.signalAll();
        } finally {
            mutex.unlock();
        }
    }

    public int get() {
        try {
            mutex.lock();
            if (!available) {
                try {
                    isAvail.await();
                } catch (InterruptedException ignored) { }
            }
            return value;
        } finally {
            mutex.unlock();
        }
    }
}
