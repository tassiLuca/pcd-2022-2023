package pcd.lab03.liveness.observer;

/**
 * Simple agent that continually performs the update of the {@link Observed} entity.
 */
class UpdateAgent extends Thread {
    private final Observed obj;

    public UpdateAgent(ObservedImpl obj) {
        this.obj = obj;
    }

    public void run() {
        while (true) {
            obj.update();
        }
    }
}
