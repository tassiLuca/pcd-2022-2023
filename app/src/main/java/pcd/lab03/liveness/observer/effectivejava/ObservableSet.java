package pcd.lab03.liveness.observer.effectivejava;

public interface ObservableSet<E> {

    void addObserver(SetObserver<E> observer);

    boolean removeObserver(SetObserver<E> observer);
}
