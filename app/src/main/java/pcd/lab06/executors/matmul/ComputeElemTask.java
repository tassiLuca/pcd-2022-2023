package pcd.lab06.executors.matmul;

public class ComputeElemTask implements Runnable {
	
	private final Mat a;
	private final Mat b;
	private final Mat target;
	private final int i;
	private final int j;
	
	public ComputeElemTask(int i, int j, Mat a, Mat b, Mat target){
		this.i = i;
		this.j = j;
		this.a = a;
		this.b = b;
		this.target = target;
	}
	
	public void run() {
		log("computing (" + i + ", " + j + ")...");
		double sum = 0;
		for (int k = 0; k < a.getNColumns(); k++) {
			sum += a.get(i, k) * b.get(k, j);
		}
		target.set(i, j, sum);
		log("computing (" + i + ", " + j + ") done: " + sum);
	}
	
	private void log(String msg){
		System.out.println("[TASK] " + msg);
	}
}
