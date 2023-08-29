package pcd.lab03.liveness.deadlocked_resource;

public class Resource {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftRight(String msg) {
        synchronized(left) {
            synchronized(right) {
                log(msg + " entered");
            }
        }
    }

    public void rightLeft(String msg) {
        synchronized(right) {
            synchronized(left) {
                log(msg + " entered");
            }
        }
//		// simple solution :)
//		synchronized(left) {
//			synchronized(right) {
//				log(msg + " entered");
//			}
//		}
    }

    private void log(String message) {
        System.out.println(message);
    }
}
