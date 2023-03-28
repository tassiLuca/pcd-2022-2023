package pcd.lab03.liveness.deadlocked_resource;

public class ThreadB extends BaseAgent {
 
	private final Resource res;
	
	public ThreadB(Resource res){
		this.res = res;
	}
	
	public void run() {
		while (true) {
			waitAbit();
			res.leftRight("Thread B");
		}
	}	
}
