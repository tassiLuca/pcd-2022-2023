package pcd.lab02.check_act_fixed;

public class WorkerB extends Thread {

    private final Counter counter;
    private final int ntimes;

    public WorkerB(Counter counter, int ntimes) {
        this.counter = counter;
        this.ntimes = ntimes;
    }

    public void run() {
        try {
            for (int i = 0; i < ntimes; i++) {
                synchronized (counter) {
                    if (counter.getValue() < 1) {
                        counter.inc();
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
