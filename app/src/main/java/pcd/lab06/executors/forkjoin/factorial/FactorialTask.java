package pcd.lab06.executors.forkjoin.factorial;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.atomic.AtomicReference;

public class FactorialTask extends CountedCompleter<BigInteger> {

    private static final int SEQUENTIAL_THRESHOLD = 5;
    private List<BigInteger> integers;
    private final AtomicReference<BigInteger> result;

    public FactorialTask(CountedCompleter<BigInteger> parent, AtomicReference<BigInteger> result, List<BigInteger> integers) {
        super(parent);
        this.integers = integers;
        this.result = result;
    }

    @Override
    public void compute() {
        log("Ints => " + integers);
        if (integers.size() <= SEQUENTIAL_THRESHOLD) {
            integers.forEach(i -> result.getAndAccumulate(computeFactorial(i), BigInteger::add));
            propagateCompletion();
        } else {
            final int middle = integers.size() / 2;
            final List<BigInteger> newIntegers = integers.subList(middle, integers.size());
            integers = integers.subList(0, middle);
            addToPendingCount(1);
            log("Splitting...");
            new FactorialTask(this, result, newIntegers).fork();
            compute();
        }
    }

    @Override
    public BigInteger getRawResult () {
        return result.get();
    }

    private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }

    private BigInteger computeFactorial(BigInteger n){
        if (n.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        } else {
            return(n.multiply(computeFactorial(n.subtract(BigInteger.ONE))));
        }
    }
}
