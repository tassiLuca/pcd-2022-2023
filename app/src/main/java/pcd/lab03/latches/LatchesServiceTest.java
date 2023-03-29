package pcd.lab03.latches;

import java.util.concurrent.CountDownLatch;

public class LatchesServiceTest {

    public static void main(String... ars) throws InterruptedException {
        final var latch = new CountDownLatch(2);
        final var mainService = new MainService(latch);
        final var configService1 = new ConfigService(1, latch);
        final var configService2 = new ConfigService(2, latch);
        mainService.start();
        configService1.start();
        configService2.start();
        mainService.join();
    }
}
