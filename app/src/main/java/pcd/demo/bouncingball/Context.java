package pcd.demo.bouncingball;

import java.util.*;

import pcd.demo.common.*;

public class Context {

    private final Boundary bounds;
    private final ArrayList<BallAgent> balls;
    
    public Context() {
        bounds = new Boundary(-1.0,-1.0,1.0,1.0);
        balls = new ArrayList<>();
    } 
    
    public synchronized void createNewBall() {
        final BallAgent agent = new BallAgent(this);
        balls.add(agent);
        agent.start();
    }
    
    public synchronized void removeBall() {
        if (balls.size() > 0){
            final BallAgent ball = balls.get(0);
            balls.remove(ball);
            ball.terminate();
       	}
    }
    
    public synchronized P2d[] getPositions() {
    	final P2d[] array = new P2d[balls.size()];
        for (int i = 0; i < array.length; i++){
            array[i] = balls.get(i).getPos();
        }
        return array;
    }
    
    public Boundary getBounds(){
        return bounds;
    }
}
