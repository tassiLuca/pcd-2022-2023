package pcd.lab03.liveness.observer;

/**
 * An interface modeling an entity which observes a possible multitude of {@link Observed} one.
 */
public interface Observer {

    /**
     * Register the given entity in order to observe it.
     * @param obs the entity to be observed.
     */
    void observe(Observed obs);

    /**
     * Callback which is performed when a state change occurs.
     * @param obs the {@link Observed} that changed its state.
     */
    void notifyStateChanged(Observed obs);

    /**
     * @return the overall state of all observed entities.
     */
    int getOverallState();
}
