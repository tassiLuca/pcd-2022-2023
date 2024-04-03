package pcd.lab03.liveness.observer;

/**
 * An agent simply listening for state change and logging them on standard output.
 */
class ListeningAgent extends Thread {
    private final ObserverImpl obj;

    public ListeningAgent(ObserverImpl obj) {
        this.obj = obj;
    }

    public void run() {
        while (true) {
            log("overall state: " + obj.getOverallState());
        }
    }

    private void log(String msg) {
        synchronized(System.out) {
            System.out.println("[" + this + "] " + msg);
        }
    }
}
