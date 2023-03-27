package pcd.lab04.monitors;

public class TestException {

    public static void main(String[] args) {
        final var lock = new Object();
        try {
            lock.wait();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
