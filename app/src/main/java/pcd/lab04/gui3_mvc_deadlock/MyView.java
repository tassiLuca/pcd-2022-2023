package pcd.lab04.gui3_mvc_deadlock;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class MyView extends JFrame implements ActionListener, ModelObserver {

    private final MyController controller;
    private final JTextField state;

    public MyView(MyController controller) {
        super("My View");
        this.controller = controller;
        setSize(400, 60);
        setResizable(false);
        JButton button1 = new JButton("Event #1");
        button1.addActionListener(this);
        JButton button2 = new JButton("Event #2");
        button2.addActionListener(this);
        state = new JTextField(10);
        JPanel panel = new JPanel();
        panel.add(button1);
        panel.add(button2);
        panel.add(state);
        setLayout(new BorderLayout());
        add(panel,BorderLayout.NORTH);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }
        });
    }

    public void display() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public void actionPerformed(ActionEvent ev) {
        try {
            controller.processEvent(ev.getActionCommand());
        } catch (Exception ignored) { }
    }

    @Override
    public void modelUpdated(MyModel model) {
        try {
            System.out.println("[View] model updated => updating the view");
            /* wrong: possible races with the EDT */
            // state.setText("state: " + model.getState());
            /* no more races but deadlock: the calling thread **blocks** until the EDT has executed it but:
             * - this method was called by the model `update()` one with mutual exclusion (the model is a monitor!)
             * - the EDT calls the `getState()` method but **blocks** because the calling thread has still the lock
             *   on it (note that `invokeAndWait()` cannot release the lock on the model monitor)
             * --> DEADLOCK!
             */
            SwingUtilities.invokeAndWait(() -> state.setText("state: " + model.getState()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
