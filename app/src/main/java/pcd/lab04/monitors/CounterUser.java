package pcd.lab04.monitors;

import pcd.lab04.monitors.sync_cell.Worker;

public class CounterUser extends Worker {
	
	private Counter counter;
	
	public CounterUser(Counter counter){
		super("Counter user");
		this.counter = counter;
	}
	
	public void run(){
		while (true){
			counter.inc();
		}
	}
}
