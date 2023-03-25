package pcd.lab02.lost_updates_fixed;


public class Worker extends Thread {
	
	private final SafeCounter counter;
	private final int ntimes;
	
	public Worker(SafeCounter counter, int ntimes) {
		this.counter = counter;
		this.ntimes = ntimes;
	}
	
	public void run(){
		for (int i = 0; i < ntimes; i++) {
			counter.inc();
		}
	}
}
