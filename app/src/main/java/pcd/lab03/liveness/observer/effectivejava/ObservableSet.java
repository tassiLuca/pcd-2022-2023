package pcd.lab03.liveness.observer.effectivejava;

import java.util.Set;

public interface ObservableSet<E> {

    void addObserver(SetObserver<E> observer);

    boolean removeObserver(SetObserver<E> observer);
}
