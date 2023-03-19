package pcd.demo.bouncingball;

import javax.swing.*;
import java.awt.event.*;

public class ControlPanel extends JFrame implements ActionListener {
    public static final int PANEL_WIDTH = 250;
    public static final int PANEL_HEIGHT = 60;
    public static final String BUTTON_PLUS_TEXT = "+ ball";
    public static final String BUTTON_MINUS_TEXT = "- ball";
    private final JButton buttonPlus;
    private final JButton buttonMinus;
    private final Context context;
    
    public ControlPanel(final Context ctx){
        context = ctx;
        setTitle("Control Panel");
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setResizable(false);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(final WindowEvent ev){
				System.exit(-1);
			}
		});
        buttonPlus = new JButton(BUTTON_PLUS_TEXT);
        buttonMinus = new JButton(BUTTON_MINUS_TEXT);
        JPanel panel = new JPanel();
        panel.add(buttonPlus);
        panel.add(buttonMinus);
        getContentPane().add(panel);
        buttonPlus.addActionListener(this);
        buttonMinus.addActionListener(this);
    }
    
    public void actionPerformed(final ActionEvent ev) {
        Object src = ev.getSource();
        if (src == buttonPlus){
            context.createNewBall();
        } else if (src == buttonMinus){
            context.removeBall();
        } else {
            throw new IllegalStateException("No callback on " + ev);
        }
    }
}
