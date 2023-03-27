package pcd.lab04.gui4_mvc_no_deadlock;

public class MyAgent extends Thread {

	private final MyModel model;
	
	public MyAgent(MyModel model){
		this.model = model;
	}
	
	public void run() {
		while (true) {
			try {
				model.update();
				Thread.sleep(500);
			} catch (Exception ignored) { }
		}
	}
}
