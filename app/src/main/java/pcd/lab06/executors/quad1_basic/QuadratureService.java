package pcd.lab06.executors.quad1_basic;

import java.util.concurrent.*;

public class QuadratureService {

    private final int numTasks;
    private final int poolSize;

    public QuadratureService (int numTasks, int poolSize){
        this.numTasks = numTasks;
        this.poolSize = poolSize;
    }

    public double compute(Function mf, double a, double b) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        QuadratureResult result = new QuadratureResult();
        double x0 = a;
        double step = (b - a) / numTasks;
        for (int i = 0; i < numTasks; i++) {
            try {
                executor.execute(new ComputeAreaTask(x0, x0 + step, mf, result));
                log("submitted task " + x0 + " " + (x0+step));
                x0 += step;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return result.getResult();
    }

    private void log(String msg){
        System.out.println("[SERVICE] "+msg);
    }
}
