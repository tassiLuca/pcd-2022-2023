package pcd.lab01.bouncingballdemo.bouncingballnet;

import java.util.*;

import pcd.lab01.bouncingballdemo.common.*;

public class BallAgent extends Thread {
    
    private P2d pos;
    private final V2d vel;
    private boolean stop;
    private final double speed;
    private final Context ctx;
    private final Boundary bounds;
    private long lastUpdate;
    
    public BallAgent(Context ctx){
    		this.ctx = ctx;
         pos = new P2d(0,0);
         Random rand = new Random(System.currentTimeMillis());
         double dx = rand.nextDouble();
         vel = new V2d(dx,Math.sqrt(1-dx*dx));
         speed = rand.nextDouble()*3;
         bounds = ctx.getBounds();
         stop = false;
    }

    public BallAgent(Context ctx, P2d pos, V2d v, double speed){
	    this.ctx = ctx;
        this.pos = pos;
        vel = v;
        this.speed = speed;
        bounds = ctx.getBounds();
        stop = false;
    }
    
    public void run() {
        log("INIT: vel "+vel+"speed "+speed);
        try {
            lastUpdate = System.currentTimeMillis();
	        while (!stop){
	            updatePos();
	            Thread.sleep(20);	
	        }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        log("TERMINATED");
    }
    
    public void terminate(){
        stop = true;
    }
    
    private synchronized void updatePos(){
        long time = System.currentTimeMillis();
        long dt = time - lastUpdate;
        lastUpdate = time;
        pos = pos.sum(vel.mul(speed*dt*0.001));
        applyConstraints();
    }

    private void applyConstraints(){
        if (pos.x > bounds.x1()){
            if (ctx.goneOutsideRight(this,pos,vel,speed)){
                stop = true;
            } else {
                pos.x = bounds.x1();
                vel.x = -vel.x;
            }
        } else if (pos.x < bounds.x0()){
            if (ctx.goneOutsideLeft(this,pos,vel,speed)){
                stop = true;
            } else {
                pos.x = bounds.x0();
                vel.x = -vel.x;
            }
        } else if (pos.y > bounds.y1()){
            pos.y = bounds.y1();
            vel.y = -vel.y;
        } else if (pos.y < bounds.y0()){
            pos.y = bounds.y0();
            vel.y = -vel.y;
        }
    }
    
    public synchronized P2d getPos(){
        return new P2d(pos.x,pos.y);
    }
    
    private void log(String msg){
        System.out.println("[BALL] "+msg);
    }

}
