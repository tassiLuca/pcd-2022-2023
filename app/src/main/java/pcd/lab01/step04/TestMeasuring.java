package pcd.lab01.step04;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TestMeasuring {

    public static void main(String[] args) {
        final int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("N cores: " + cores);
        long t0 = System.currentTimeMillis();
        List<Thread> tlist = new ArrayList<>();
        IntStream.rangeClosed(0, cores - 1).forEach(i -> {
            Thread t = new Thread(() -> {
                System.out.println("Hello from core " + i + " - " + Thread.currentThread().getName());
                double waste = 0;
                for (int j = 0; j < 100000; j++){
                    for (int k = 0; k < 1000; k++){
                        waste = waste + k * Math.sin(j);
                    }
                }
            });
            t.start();
            tlist.add(t);
        });
        tlist.stream().forEach(t -> {
            try {
                t.join();
            } catch (Exception ignored) { }
        });
        long t1 = System.currentTimeMillis();
        System.out.println("Time elapsed: "+ (t1-t0));
    }
}
