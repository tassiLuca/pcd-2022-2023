package pcd.lab02.jpf;

public class MyThread extends Thread {
    private Counter c;

    public MyThread(Counter c) {
        this.c = c;
    }

    public void run() {
        if (c.getCount() == 0) {
            c.inc();
        }
    }
}
