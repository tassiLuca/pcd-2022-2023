package pcd.lab01.bouncingballdemo.bouncingball;

import java.util.*;

import pcd.lab01.bouncingballdemo.common.*;

public class BallAgent extends Thread {
    
    private P2d pos;
    private final V2d vel;
    private boolean stop;
    private final double speed;
    private final Context context;
    private long lastUpdate;
    
    public BallAgent(final Context ctx){
    	context = ctx;
        pos = new P2d(0,0);
        Random rand = new Random(System.currentTimeMillis());
        double dx = rand.nextDouble();
        vel = new V2d(dx, Math.sqrt(1 - dx * dx));
        speed = rand.nextDouble() * 3;
        stop = false;
    }

    public void run() {
        try {
            lastUpdate = System.currentTimeMillis();
	        while (!stop) {
	            updatePosition();
                Thread.sleep(5);
	        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void terminate() {
        stop = true;
    }
    
    private synchronized void updatePosition() {
        long time = System.currentTimeMillis();
        long dt = time - lastUpdate;
        lastUpdate = time;
        pos = pos.sum(vel.mul(speed * dt * 0.001));
        applyConstraints();
    }

    private void applyConstraints(){
        Boundary bounds = context.getBounds();
        if (pos.x > bounds.x1()) {
            pos.x = bounds.x1();
            vel.x = -vel.x;
        } else if (pos.x < bounds.x0()) {
            pos.x = bounds.x0();
            vel.x = -vel.x;
        } else if (pos.y > bounds.y1()) {
            pos.y = bounds.y1();
            vel.y = -vel.y;
        } else if (pos.y < bounds.y0()) {
            pos.y = bounds.y0();
            vel.y = -vel.y;
        }
    }
    
    public synchronized P2d getPos(){
        return new P2d(pos.x, pos.y);
    }
}
