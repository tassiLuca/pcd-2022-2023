package pcd.lab04.monitors.sync_cell;

public class Setter extends Worker {
	
	private final SyncCell cell;
	private final int value;
	
	public Setter(SyncCell cell, int value){
		super("setter");
		this.cell = cell;
		this.value = value;
	}
	
	public void run(){
		log("before setting");
		cell.set(value);
		log("after setting");
	}
}
