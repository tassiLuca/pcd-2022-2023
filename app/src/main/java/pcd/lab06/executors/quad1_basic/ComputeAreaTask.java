package pcd.lab06.executors.quad1_basic;

public class ComputeAreaTask implements Runnable {

	private final QuadratureResult result;
	private final Function mf;
	private final double a;
	private final double b;

	public ComputeAreaTask(double a, double b, Function mf, QuadratureResult result) {
		this.mf = mf;
		this.a = a;
		this.b = b;
		this.result = result;
	}

	public void run() {
		log("executing task " + a + " " + b);
		double sum = 0;
		double step = (b - a) / 1000;
		double x = a;
		for (int i = 0; i < 1000; i++) {
			sum += step * mf.eval(x);
			x += step;
		}
		result.add(sum);
		log("added result " + a + " " + b + " " + sum);
	}

	private void log(String msg) {
		System.out.println(msg);
	}
}
