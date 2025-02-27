package pcd.lab01.bouncingballdemo.bouncingballnet;

import javax.swing.*;
import java.awt.event.*;

public class ControlPanel extends JFrame implements ActionListener{
    private final JButton buttonPlus;
    private final Context context;
    
    public ControlPanel(Context ctx){
        context = ctx;
        setTitle("Control Panel");
        setSize(250,60);
        setResizable(false);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
		});

        buttonPlus = new JButton("+ ball");
        JButton buttonMinus = new JButton("- ball");
        JPanel p = new JPanel();
        p.add(buttonPlus);
        p.add(buttonMinus);
        getContentPane().add(p);
        buttonPlus.addActionListener(this);
        buttonMinus.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent ev){
        Object src = ev.getSource();
        if (src==buttonPlus){
            context.createNewBall();
        } else {
            context.removeBall();
        }
    }
}
