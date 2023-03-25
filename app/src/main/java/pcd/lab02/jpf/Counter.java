package pcd.lab02.jpf;

public class Counter {
    private int count;

    public Counter() {
        count = 0;
    }

    public void inc() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
