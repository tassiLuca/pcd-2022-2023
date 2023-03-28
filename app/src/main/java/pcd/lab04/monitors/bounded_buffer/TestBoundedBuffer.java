package pcd.lab04.monitors.bounded_buffer;

public class TestBoundedBuffer {

	public static void main(String[] args){
		//BoundedBuffer<Integer> buffer = new BoundedBuffer1<Integer>(4);
		BoundedBuffer<Integer> buffer = new BoundedBuffer2<Integer>(4);
		int nProducers = 1;
		int nConsumers = 1;
		for (int i = 0; i < nProducers; i++){
			new Producer(buffer).start();
		}
		for (int i = 0; i < nConsumers; i++){
			new Consumer(buffer).start();
		}
	}	
}
