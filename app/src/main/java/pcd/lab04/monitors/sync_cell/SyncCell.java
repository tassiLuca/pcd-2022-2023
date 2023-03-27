package pcd.lab04.monitors.sync_cell;

public class SyncCell {

	private int value;
	private boolean available;

	public SyncCell(){
		available = false;
	}

	public synchronized void set(int v) {
		value = v;
		available = true;
		notifyAll();  
	}

	public synchronized int get() {
		while (!available) {
			try {
				wait();
			} catch (InterruptedException ignored) { }
		}
		return value;
	}
}
