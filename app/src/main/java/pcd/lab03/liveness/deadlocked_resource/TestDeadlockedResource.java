package pcd.lab03.liveness.deadlocked_resource;

/**
 * Deadlock example.
 */
public class TestDeadlockedResource {
	public static void main(String[] args) {
		Resource res = new Resource();
		new ThreadA(res).start();
		new ThreadB(res).start();
	}
}
