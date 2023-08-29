package pcd.lab02.check_act_fixed;

public class WorkerA extends Thread {

    private final Counter counter;
    private final int ntimes;

    public WorkerA(Counter counter, int ntimes) {
        this.counter = counter;
        this.ntimes = ntimes;
    }

    public void run() {
        try {
            for (int i = 0; i < ntimes; i++) {
                synchronized (counter) {
                    if (counter.getValue() > 0) {
                        counter.dec();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
