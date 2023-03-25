package pcd.lab02.check_act;

/**
 * To enable assertions: run with -ea option.
 */
public class TestCounter {

	public static final int NTIMES = 10_000;

	public static void main(String[] args) throws Exception {
		final Counter counter = new Counter(0,1);
		WorkerA w1a = new WorkerA(counter, NTIMES);
		WorkerA w1b = new WorkerA(counter, NTIMES);
		WorkerB w2a = new WorkerB(counter, NTIMES);
		WorkerB w2b = new WorkerB(counter, NTIMES);
		w1a.start();
		w1b.start();
		w2a.start();
		w2b.start();
		w1a.join();
		w1b.join();
		w2a.join();
		w2b.join();
		System.out.println("Counter final value: " + counter.getValue());
	}
}
