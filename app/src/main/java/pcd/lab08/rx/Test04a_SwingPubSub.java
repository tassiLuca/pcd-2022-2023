package pcd.lab08.rx;

import javax.swing.*;

import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.awt.event.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Test04a_SwingPubSub {

    static class MyFrame extends JFrame {

        public MyFrame(PublishSubject<Integer> stream) {
            super("Swing + RxJava");
            setSize(150,60);
            setVisible(true);
            JButton button = new JButton("Press me");
            button.addActionListener((ActionEvent ev) -> stream.onNext(1));
            getContentPane().add(button);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent ev) {
                    System.exit(-1);
                }
            });
        }
    }

    static public void main(String[] args) {
        PublishSubject<Integer> clickStream = PublishSubject.create();
        SwingUtilities.invokeLater(()-> new MyFrame(clickStream));
        clickStream.observeOn(Schedulers.computation())
            .subscribe(v -> log("click: " + System.currentTimeMillis()));
        clickStream.buffer(clickStream.throttleWithTimeout(250, TimeUnit.MILLISECONDS))
            .map(List::size)
            .filter(v -> v >= 2)
            .subscribe(v -> log("Multi-click: " + v));
    }

    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }
}
