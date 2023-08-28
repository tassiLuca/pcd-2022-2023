package pcd.lab01.step04;

public class Step04_Parallel {

    public static void main(String[] args) throws Exception {
        final int size = 2_000;
        final int n = size;
        final int k = size;
        final int m = size;
        boolean debugging = false;
        System.out.println("Testing A[" + n + "," + k +"] * B[" + k + "," + m + "]");
        System.out.println("Initialising...");
        final Matrix matrixA = new Matrix(n,m);
        matrixA.initRandom(10);
        if (debugging) {
            System.out.println("A:");
            matrixA.print();
        }
        final Matrix matrixB = new Matrix(m,k);
        matrixB.initRandom(10);
        if (debugging) {
            System.out.println("B:");
            matrixB.print();
        }
        System.out.println("Initialising done.");
        System.out.println("Computing matmul...");
        final int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        MatMulConcurLib.init(nWorkers);
        final Chrono cron = new Chrono();
        cron.start();
        Matrix result = MatMulConcurLib.multiply(matrixA, matrixB);
        cron.stop();
        System.out.println("Computing matmul done.");
        if (debugging) {
            System.out.println("Result:");
            result.print();
        }
        System.out.println("Time elapsed: " + cron.getTime() + " ms.");
    }
}
