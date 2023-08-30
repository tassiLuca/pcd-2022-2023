package pcd.lab04.monitors.barrier;

/**
 * An interface modeling a **one-shot Barrier**, i.e. a synchronization
 * point in which all threads in the group cannot proceed until all
 * other threads reach this barrier.
 */
interface Barrier {

    /**
     * Blocks until all other participant threads reach this barrier.
     * @throws InterruptedException if any thread interrupted the current
     * thread before or while the current thread was waiting.
     */
    void hitAndWaitAll() throws InterruptedException;
}
