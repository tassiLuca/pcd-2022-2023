package pcd.lab01.step04;

public class Step04_Sequential {

    public static void main(String[] args) throws Exception {
        final int size = 2_000; /* square matrix */
        final int n = size; /* num rows mat A */
        final int k = size; /* num columns mat A = num rows mat B */
        final int m = size; /* nun columns mat B */
        final boolean debugging = false;
        System.out.println("Testing A[" + n + "," + k + "] * B[" + k + "," + m + "]");
        System.out.println("Initialising...");
        final Matrix matrixA = new Matrix(n,m);
        matrixA.initRandom(10);
        if (debugging) {
            System.out.println("A:");
            matrixA.print();
        }
        final Matrix matrixB = new Matrix(m,k);
        matrixB.initRandom(10);
        if (debugging){
            System.out.println("B:");
            matrixB.print();
        }
        System.out.println("Initialising done.");
        System.out.println("Computing matmul...");
        final Chrono cron = new Chrono();
        cron.start();
        final Matrix result = Matrix.multiply(matrixA, matrixB);
        cron.stop();
        System.out.println("Computing matmul done.");
        if (debugging) {
            System.out.println("Result:");
            result.print();
        }
        System.out.println("Time elapsed: " + cron.getTime() + " ms.");
    }
}
