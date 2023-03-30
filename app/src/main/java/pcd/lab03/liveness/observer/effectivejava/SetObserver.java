package pcd.lab03.liveness.observer.effectivejava;

@FunctionalInterface
public interface SetObserver<E> {

    void added(ObservableSet<E> set, E element);
}
