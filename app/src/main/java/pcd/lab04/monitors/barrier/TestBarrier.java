package pcd.lab04.monitors.barrier;

import java.util.ArrayList;
import java.util.List;

public class TestBarrier {

	public static void main(String[] args) {
		final int nWorkers = 10;
		final Barrier barrier = new BarrierImpl(nWorkers);
		final List<Worker> workers = new ArrayList<>();
		for (int i = 0; i < nWorkers; i++) {
			workers.add(new Worker("Worker-" + i, barrier));
		}
		for (Worker w: workers) {
			w.start();
		}
	}
}
