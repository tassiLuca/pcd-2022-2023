package pcd.lab01.step04;

public class Worker extends Thread {

    private final Matrix a;
    private final Matrix b;
    private final Matrix result;
    private final int from;
    private final int nrows;

    public Worker(final int from, final int numOfRows, final Matrix a, final Matrix b, final Matrix result) {
        this.from = from;
        this.nrows = numOfRows;
        this.a = a;
        this.b = b;
        this.result = result;
    }

    public void run() {
        long count = 0;
        final int to = from + nrows;
        log("started: " + from + " to " + (to-1));
        for (int i = from; i < to; i++) {
            for (int j = 0; j < result.getNColumns(); j++) {
                double sum = 0;
                for (int k = 0; k < a.getNColumns(); k++) {
                    sum += a.get(i, k)*b.get(k, j);
                    count++;
                }
                result.set(i, j, sum);
            }
        }
        log("done " + count);
    }

    private void log(String msg) {
        System.out.println("[WORKER] "+msg);
    }
}
