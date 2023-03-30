package pcd.lab03.liveness.observer.effectivejava;

import java.util.stream.IntStream;

public class TestSafeObservableSet {

    public static void main(String... args) {
        final SafeObservableSet<Integer> set = new SafeObservableSet<>();
        // set.addObserver((s, e) -> System.out.println("Added element " + e));
        set.addObserver(new SetObserver<Integer>() {
            @Override
            public void added(ObservableSet<Integer> set, Integer element) {
                System.out.println("Added element " + element);
                if (element == 23) {
                    set.removeObserver(this);
                }
            }
        });
        IntStream.range(0, 100).forEach(set::add);
    }
}
