package pcd.lab02.observable_set;

public interface SetObserver<E> {

    void added(ObservableSet<E> set, E element);
}
