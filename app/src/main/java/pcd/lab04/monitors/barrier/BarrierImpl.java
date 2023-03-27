package pcd.lab04.monitors.barrier;

public class BarrierImpl implements Barrier {

	private int numberOfWaiters;

	public BarrierImpl(final int numOfParticipants) {
		numberOfWaiters = numOfParticipants;
	}

	@Override
	public synchronized void hitAndWaitAll() throws InterruptedException {
		numberOfWaiters = numberOfWaiters - 1;
		if (numberOfWaiters == 0) {
			notifyAll();
		} else {
			while (numberOfWaiters != 0) {
				wait();
			}
		}
	}
}
