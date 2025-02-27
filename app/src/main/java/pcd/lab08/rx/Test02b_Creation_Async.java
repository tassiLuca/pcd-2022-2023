package pcd.lab08.rx;

import io.reactivex.rxjava3.core.*;

public class Test02b_Creation_Async {

    public static void main(String[] args) throws Exception {
        log("Creating an observable (cold) using its own thread.");
        Observable<Integer> source = Observable.create(emitter ->
            new Thread(() -> {
                int i = 0;
                while (i < 20) {
                    try {
                        log("source: "+i);
                        emitter.onNext(i);
                        Thread.sleep(200);
                        i++;
                    } catch (Exception ignored){ }
                }
            }).start()
        );
        Thread.sleep(1000);
        log("Subscribing A.");
        source.subscribe((s) -> log("Subscriber A: " + s));
        Thread.sleep(1000);
        log("Subscribing B.");
        source.subscribe((s) -> log("Subscriber B: " + s));
        System.out.println("Done.");
    }

    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }
}
