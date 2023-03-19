package pcd.demo.bouncingball;

import pcd.demo.common.P2d;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;

public class ViewerFrame extends JFrame {

    public static final String FRAME_TITLE = "Bouncing Balls";
    private final VisualiserPanel panel;
    
    public ViewerFrame(final int width, final int height) {
        setTitle(FRAME_TITLE);
        setSize(width, height);
        setResizable(false);
        panel = new VisualiserPanel(width,height);
        getContentPane().add(panel);
        addWindowListener(new WindowAdapter(){
			public void windowClosing(final WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(final WindowEvent ev){
				System.exit(-1);
			}
		});
    }
    
    public void updatePosition(final P2d[] pos){
        panel.updatePositions(pos);
    }
        
    private static class VisualiserPanel extends JPanel {
        private P2d[] positions;
        private final long dx;
        private final long dy;
        
        public VisualiserPanel(final int width, final int height){
            setSize(width,height);
            dx = width/2 - 20;
            dy = height/2 - 20;
        }

        public void paint(final Graphics g){
    		Graphics2D g2 = (Graphics2D) g;
    		g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    		g2.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
    		g2.clearRect(0, 0, this.getWidth(), this.getHeight());
            synchronized (this) {
	            if (positions != null) {
	                Arrays.stream(positions).forEach(p -> {
	                	int x0 = (int)(dx + p.x * dx);
		                int y0 = (int)(dy - p.y * dy);
		                g2.drawOval(x0, y0, 20, 20);
		            });
	            }
                assert positions != null;
                g2.drawString("Balls (Threads): " + positions.length, 2, 20);
            }
        }
        
        public void updatePositions(P2d[] pos) {
            synchronized(this) {
                positions = pos;
            }
            repaint();
        }
    }
}
