package pcd.lab08.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ChangePropagation {

    public static void main(String[] args) throws InterruptedException {
        // Create PublishSubjects for x and y
        PublishSubject<Integer> xSubject = PublishSubject.create();
        PublishSubject<Integer> ySubject = PublishSubject.create();

        // Combine Observables and perform the reactive operation
        Disposable disposable = Observable.combineLatest(
            xSubject,
            ySubject,
            Integer::sum
        ).subscribe(
            result -> System.out.println("Result (z): " + result),
            Throwable::printStackTrace
        );

        // Simulate changes in x and y
        // Note: In a real-world scenario, you would update x and y based on events or user interactions
        xSubject.onNext(3);
        ySubject.onNext(4);

        // Simulate changes in x and y again
        xSubject.onNext(1);
        ySubject.onNext(1);

        // Dispose the subscription when done
        disposable.dispose();
    }
}
