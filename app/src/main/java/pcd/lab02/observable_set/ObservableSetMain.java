package pcd.lab02.observable_set;

import java.util.HashSet;

public class ObservableSetMain {
    
    public static void main(String... args) {
        final var set = new ObservableSet<Integer>(new HashSet<>());
        // set.addObserver((s, e) -> System.out.println("Added element " + e));
        set.addObserver(new SetObserver<Integer>() {
            @Override
            public void added(ObservableSet<Integer> set, Integer element) {
                System.out.println("Added element " + element);
                if (element == 25) {
                    set.removeObserver(this);
                }
            }
        });
        for (int i = 0; i < 100; i++) {
            set.add(i);
        }
    }
}
