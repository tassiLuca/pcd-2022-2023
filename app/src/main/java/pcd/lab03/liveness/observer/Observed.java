package pcd.lab03.liveness.observer;

/**
 * An interface modelling an entity which is observed.
 */
public interface Observed {

    /**
     * Update its state.
     */
    void update();

    /**
     * @return the current state.
     */
    int getState();

    /**
     * Registers a new observer (listener) in order to be notified
     * when a state change occurs.
     * @param obj the observer to register.
     */
    void register(Observer obj);
}
