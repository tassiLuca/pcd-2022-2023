package pcd.lab02.jpf;

import gov.nasa.jpf.vm.Verify;

/**
 * Check race condition: check-and-act concurrency hazard
 */
public class TestCheckAct {

	public static void main(String[] args) throws Exception {
		Verify.beginAtomic();
		Counter c = new Counter();
		Thread th0 = new MyThread(c);
		Thread th1 = new MyThread(c);
		th0.start();
		th1.start();
		Verify.endAtomic();
		th0.join();
		th1.join();
		int value = c.getCount();
		assert value == 1;
	}
}
