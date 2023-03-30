package pcd.lab03.liveness.observer.effectivejava;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class SafeObservableSet<E> extends HashSet<E> implements ObservableSet<E> {

    private final List<SetObserver<E>> observers = new CopyOnWriteArrayList<>();
    private final Set<E> set = new HashSet<>();

    @Override
    public void addObserver(SetObserver<E> observer) {
        observers.add(observer);
    }

    @Override
    public boolean removeObserver(SetObserver<E> observer) {
        return observers.remove(observer);
    }

    public boolean add(E element) {
        boolean added = set.add(element);
        if (added) {
            notifyElementAdded(element);
        }
        return added;
    }

    public boolean addAll(Collection<? extends E> collection) {
        boolean result = false;
        for (var element : collection) {
            result |= add(element);
        }
        return result;
    }

    private void notifyElementAdded(E element) {
        for (var observer : observers) {
            observer.added(this, element);
        }
    }
}
