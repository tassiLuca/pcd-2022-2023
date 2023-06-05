package pcd.lab08.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyTest {

    public static void main(String... args) {
        log("Test Schedulers");
        // Observable.just("Hello, RxJava")
        //         .observeOn(Schedulers.newThread())
        //         .doOnNext(MyTest::log)
        //         .observeOn(Schedulers.newThread())
        //         .subscribe(MyTest::log);
        Disposable subscription = Observable.just("Hello, World!")
                .subscribe(s -> System.out.println(s));
    }

    private static void log(String msg) { System.out.println("[" + Thread.currentThread().getName() + "] " + msg); }
}
