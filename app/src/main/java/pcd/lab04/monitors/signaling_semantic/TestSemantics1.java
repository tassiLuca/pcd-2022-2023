package pcd.lab04.monitors.signaling_semantic;

public class TestSemantics1 {

    static class MyMonitor {

        public synchronized void callWait() {
            try {
                System.out.println("First thread inside, going to wait");
                wait();
                System.out.println("First thread unblocked.");
                try {
                    Thread.sleep(5000);
                } catch (Exception ignored) { }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        public synchronized void callNotify() {
            System.out.println("Second thread inside");
            try {
                Thread.sleep(5000);
            } catch (Exception ignored) { }
            System.out.println("Second thread inside, going to signal");
            notify();
            System.out.println("Second thread inside signaled.");
        }

        public synchronized void enter() {
            System.out.println("Third thread inside.");
            try {
                Thread.sleep(5000);
            } catch (Exception ignored) { }
        }
    }

    static class MyThread1 extends Thread {
        private final MyMonitor mon;

        public MyThread1(MyMonitor mon) {
            this.mon = mon;
        }

        public void run() {
            log("First thread started.");
            mon.callWait();
        }

        private void log(String msg) {
            synchronized (System.out) {
                System.out.println(msg);
            }
        }
    }

    static class MyThread2 extends Thread {
        private final MyMonitor mon;

        public MyThread2(MyMonitor mon) {
            this.mon = mon;
        }

        public void run() {
            log("Second thread started.");
            mon.callNotify();
        }

        private void log(String msg) {
            synchronized (System.out) {
                System.out.println(msg);
            }
        }
    }

    static class MyThread3 extends Thread {
        private MyMonitor mon;

        public MyThread3(MyMonitor mon) {
            this.mon = mon;
        }

        public void run() {
            log("Third thread started.");
            mon.enter();
        }

        private void log(String msg) {
            synchronized (System.out) {
                System.out.println(msg);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        MyMonitor mon = new MyMonitor();
        new MyThread1(mon).start();
        new MyThread2(mon).start();
        new MyThread3(mon).start();
    }
}
