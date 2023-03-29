package pcd.lab03.latches;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConfigService extends Thread {

    private final int id;
    private final CountDownLatch latch;

    ConfigService(int id, CountDownLatch latch) {
        this.id = id;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            System.out.println("Service " + id + " is booting...");
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(50) + 4);
            System.out.println("Service " + id + " is running!");
            TimeUnit.SECONDS.sleep(new Random().nextInt(20) + 5);
            System.out.println("Service " + id + " ended!");
        } catch (InterruptedException e) {
            // not used
        } finally {
            this.latch.countDown();
        }
    }
}
