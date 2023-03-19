package pcd.demo.bouncingball;

import javax.swing.SwingUtilities;

public class BallViewer extends Thread {
    
    private final boolean stop;
    private final Context context;
    private final ViewerFrame frame;
    private static final int FRAMES_PER_SEC = 25;
    
    public BallViewer(final Context ctx){
        stop = false;
        context = ctx;
        frame = new ViewerFrame(620, 620);
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
   }
    
    public void run(){
        while (!stop) {
            long t0 = System.currentTimeMillis();
        	frame.updatePosition(context.getPositions());
            long t1 = System.currentTimeMillis();
            long dt = (1000 / FRAMES_PER_SEC) - (t1 - t0);
            if (dt > 0) {
	            try {
	                Thread.sleep(dt);     
	            } catch (Exception ex){
	            }
            }
        }
    }
}
