package pcd.lab04.monitors.sync_cell;

public class Getter extends Worker {

    private final SyncCell cell;

    public Getter(SyncCell cell) {
        super("getter");
        this.cell = cell;
    }

    public void run() {
        log("before getting");
        int value = cell.get();
        log("got value:"+value);
    }
}
