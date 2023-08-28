package pcd.lab01.step04;

public final class MatMulConcurLib {

    private final Worker[] workers;
    private static MatMulConcurLib instance;

    public static void init(final int workers) {
        instance = new MatMulConcurLib(workers);
    }

    /**
     * Initialize the library with a worker number equal to available processor + 1, since it is a cpu bound task.
     */
    public static void init() {
        instance = new MatMulConcurLib(Runtime.getRuntime().availableProcessors() + 1);
    }

    private MatMulConcurLib(final int processors){
        workers = new Worker[processors];
    }

    public static Matrix multiply(final Matrix matrixA, final Matrix matrixB) throws LibNotInitialisedException, MatMulException {
        if (instance == null) {
            throw new LibNotInitialisedException();
        }
        return instance.doMultiplicationTask(matrixA, matrixB);
    }

    private Matrix doMultiplicationTask(final Matrix matrixA, final Matrix matrixB) throws MatMulException {
        final Matrix result = new Matrix(matrixA.getNRows(), matrixB.getNColumns());
        /*
         * Strategy: divide the rows of the result matrix to be calculated equally among all the workers.
         * If the number of rows is not perfectly divisible by the worker's number,
         * the leftover rows are computed by the last worker.
         */
        final int nrows = matrixA.getNRows() / workers.length;
        int irow = 0;
        try {
            for (int i = 0; i < workers.length - 1; i++) {
                workers[i] = new Worker(irow, nrows, matrixA, matrixB, result);
                workers[i].start();
                irow += nrows;
            }
            workers[workers.length - 1] = new Worker(irow, matrixA.getNRows() - irow, matrixA, matrixB, result);
            workers[workers.length - 1].start();
            /* Wait until all workers have finished before returning the result. */
            for (Worker w: workers) {
                w.join();
            }
            return result;
        } catch (Exception ex){
            throw new MatMulException();
        }
    }
}
