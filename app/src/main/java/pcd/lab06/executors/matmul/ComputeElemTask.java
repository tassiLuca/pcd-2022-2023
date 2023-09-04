package pcd.lab06.executors.matmul;

public class ComputeElemTask implements Runnable {

    private final Matrix a;
    private final Matrix b;
    private final Matrix result;
    private final int i;
    private final int j;

    public ComputeElemTask(int i, int j, Matrix a, Matrix b, Matrix result){
        this.i = i;
        this.j = j;
        this.a = a;
        this.b = b;
        this.result = result;
    }

    public void run() {
        log("computing (" + i + ", " + j + ")...");
        double sum = 0;
        for (int k = 0; k < a.getNColumns(); k++) {
            sum += a.get(i, k) * b.get(k, j);
        }
        result.set(i, j, sum);
        log("computing (" + i + ", " + j + ") done: " + sum);
    }

    private void log(String msg){
        System.out.println("[TASK] " + msg);
    }
}
