package pcd.lab06.executors.matmul;

public class Main {

	private static final boolean DEBUGGING = false;
	private static final int SIZE = 1000;

	public static void main(String[] args) throws Exception {
		final int n = SIZE;
		int k = SIZE;
		int m = SIZE;
		System.out.println("Testing A[" + n + "," + k + "] * B[" + k + "," + m + "]");
		System.out.println("Initialising...");
		Matrix matrixA = new Matrix(n, m);
		matrixA.initRandom(10);
		if (DEBUGGING) {
			System.out.println("A:");
			matrixA.print();
		}
		Matrix matrixB = new Matrix(m, k);
		matrixB.initRandom(10);
		if (DEBUGGING) {
			System.out.println("B:");
			matrixB.print();
		}
		System.out.println("Initialising done.");
		System.out.println("Computing matmul...");
		Chrono cron = new Chrono();
		cron.start();
		Matrix matrixC = MatMulConcurLib.getInstance().multiply(matrixA, matrixB);
		cron.stop();
		System.out.println("Computing matmul done.");
		if (DEBUGGING) {
			System.out.println("C:");
			matrixC.print();
		}
		System.out.println("Time elapsed: " + cron.getTime() + " ms.");
	}
}
