package pcd.lab03.liveness.deadlocked_resource;

import java.util.Random;

public abstract class BaseAgent extends Thread {

    private final Random gen;

    public BaseAgent(){
        gen = new Random();
    }

    protected void waitABit() {
        try {
            Thread.sleep(gen.nextInt(2));
        } catch (Exception ignored){ }
    }
}
