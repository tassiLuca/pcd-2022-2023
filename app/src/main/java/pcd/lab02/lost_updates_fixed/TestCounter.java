package pcd.lab02.lost_updates_fixed;

public class TestCounter {

	private static final int N_TIMES = 100_000_000;

	public static void main(String[] args) throws Exception {
		// int ntimes = Integer.parseInt(args[0]);
		final SafeCounter counter = new SafeCounter(0);
		final Worker w1 = new Worker(counter, N_TIMES);
		final Worker w2 = new Worker(counter, N_TIMES);
		final Cron cron = new Cron();
		cron.start();
		w1.start();
		w2.start();
		w1.join();
		w2.join();
		cron.stop();
		System.out.println("Counter final value: " + counter.getValue() + " in " + cron.getTime() + "ms.");
	}
}
