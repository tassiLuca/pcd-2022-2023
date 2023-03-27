package pcd.lab04.monitors.bounded_buffer;

public interface IBoundedBuffer<Item> {

    void put(Item item) throws InterruptedException;
    
    Item get() throws InterruptedException;
    
}
