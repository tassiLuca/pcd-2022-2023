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
		Mat matA = new Mat(n, m);
		matA.initRandom(10);
		if (DEBUGGING) {
			System.out.println("A:");
			matA.print();
		}
		Mat matB = new Mat(m, k);
		matB.initRandom(10);
		if (DEBUGGING) {
			System.out.println("B:");
			matB.print();
		}
		System.out.println("Initialising done.");
		System.out.println("Computing matmul...");
		Chrono cron = new Chrono();
		cron.start();
		Mat matC = MatMulConcurLib.getInstance().matmul(matA, matB);
		cron.stop();
		System.out.println("Computing matmul done.");
		if (DEBUGGING) {
			System.out.println("C:");
			matC.print();
		}
		System.out.println("Time elapsed: " + cron.getTime() + " ms.");
	}
}
