package pcd.lab04.monitors.latch;

public class ThreadA extends Thread {

	private final Latch latch;
	
	public ThreadA(String name, Latch latch) {
		super(name);
		this.latch = latch;
	}
	
	public void run() {
		try {
			log("waiting opening.");
			latch.await();
			log("opened."); // 
		} catch (InterruptedException ex) {
			log("Interrupted!");
		}
	}
	
	private void log(String msg) {
		synchronized(System.out) {
			System.out.println("[ "+getName()+" ] "+msg);
		}
	}
}
