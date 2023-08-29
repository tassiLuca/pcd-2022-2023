package pcd.lab03.liveness.deadlocked_resource;

public class ThreadA extends BaseAgent {

    private final Resource res;

    public ThreadA(Resource res){
        this.res = res;
    }

    public void run() {
        while (true) {
            waitABit();
            res.rightLeft("Thread A");
        }
    }
}
