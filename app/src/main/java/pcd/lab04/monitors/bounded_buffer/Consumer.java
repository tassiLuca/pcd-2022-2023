package pcd.lab04.monitors.bounded_buffer;


class Consumer extends Thread {

	private final BoundedBuffer<Integer> buffer;
	
	public Consumer(BoundedBuffer<Integer> buffer){
		this.buffer = buffer;
	}

	public void run() {
		while (true) {
			try {
				Integer item = buffer.get();
				consume(item);
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		}
	}
	
	private void consume(Integer item){
		log("consumed " + item);
	}
	
	private void log(String st) {
		synchronized(System.out) {
			System.out.println("[" + this.getName() + "] " + st);
		}
	}
}
