package pcd.lab04.monitors;

public class TestWait {

    public static void main(String[] args) throws Exception  {
        Object obj = new Object();
        new Thread(() ->  {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) { }
            System.out.println("notifying...");
            synchronized (obj) {
                obj.notify();
            }
        }).start();
        new Thread(() ->  {
            try {
                synchronized (obj) {
                    obj.wait();
                }
                System.out.println("unblocked");
            } catch (InterruptedException ignored) { }
        }).start();
    }
}
