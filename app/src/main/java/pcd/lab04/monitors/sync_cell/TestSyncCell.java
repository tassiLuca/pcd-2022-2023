package pcd.lab04.monitors.sync_cell;

public class TestSyncCell {
		
	public static void main(String[] args) {
		final SyncCell cell = new SyncCell();
		new Getter(cell).start();
		new Getter(cell).start();
		new Getter(cell).start();
		try {
			Thread.sleep(2000);
		} catch (Exception ignored){}
		new Setter(cell,303).start();
	}
}
