package pcd.lab01.step05;

/*
 * On the impact on the performance / CPU utilization
 * of the agent behaviors
 */
public class Step05 {

    public static void main(String[] args) {
        new MyWorkerB("worker-B").start();
        new MyWorkerA("worker-A").start();
    }
}
