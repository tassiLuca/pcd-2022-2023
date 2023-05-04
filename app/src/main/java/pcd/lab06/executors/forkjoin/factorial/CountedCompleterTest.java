package pcd.lab06.executors.forkjoin.factorial;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class CountedCompleterTest {

    public static void main(String... args) throws ExecutionException, InterruptedException {
        final var result = ForkJoinPool.commonPool().submit(new Task());
        result.get();
        System.out.println("Completed");
    }

    private static class Task extends CountedCompleter<Void> {

        @Override
        public void compute() {
            addToPendingCount(1);
            propagateCompletion();
        }
    }
}
