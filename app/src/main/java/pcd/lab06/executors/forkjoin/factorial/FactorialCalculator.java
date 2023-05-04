package pcd.lab06.executors.forkjoin.factorial;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * Goal: given a list of integers, calculate the sum of the factorial of each.
 */
public class FactorialCalculator {

    public static void main(String... args) throws ExecutionException, InterruptedException {
        final List<BigInteger> integers = IntStream.range(0, 10_000)
            .mapToObj(i -> new BigInteger(Integer.toString(i)))
            .toList();
        final Future<BigInteger> futureResult = ForkJoinPool.commonPool()
            .submit(new FactorialTask(null, new AtomicReference<>(new BigInteger("0")), integers));
        final BigInteger sum = futureResult.get();
        System.out.println("Sum of factorials = " + sum);
    }
}
