package pcd.lab03.liveness.observer;

public class TestObserverPatternDeadlock {

    public static void main(String[] args) {
        ObservedImpl objA = new ObservedImpl();
        ObserverImpl objB = new ObserverImpl();
        objB.observe(objA);
        new UpdateAgent(objA).start();
        new ListeningAgent(objB).start();
    }
}
