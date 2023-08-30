package pcd.lab04.monitors.bounded_buffer;

import java.util.*;

class Producer extends Thread {

    private final BoundedBuffer<Integer> buffer;
    private final Random gen;

    public Producer(BoundedBuffer<Integer> buffer) {
        gen = new Random();
        this.buffer = buffer;
    }

    public void run() {
        while (true){
            Integer item = produce();
            try {
                buffer.put(item);
                log("produced "+item);
            } catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    private Integer produce() {
        return gen.nextInt(100);
    }

    private void log(String st) {
        synchronized(System.out){
            System.out.println("["+this.getName()+"] "+st);
        }
    }
}
