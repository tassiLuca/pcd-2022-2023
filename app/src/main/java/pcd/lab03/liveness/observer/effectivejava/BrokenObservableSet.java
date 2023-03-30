package pcd.lab03.liveness.observer.effectivejava;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is a <strong>broken</strong> implementation of an observable set, which
 * notifies the addition of a new element to all the subscribed observers invoking
 * an "alien" method from the synchronized block!
 * This is the code presented in Effective Java, Item 79.
 * @param <E> the type of the elements that can be added to the set
 */
public class BrokenObservableSet<E> implements ObservableSet<E> {

    private final List<SetObserver<E>> observers = new ArrayList<>();
    private final Set<E> set = new HashSet<>();

    @Override
    public void addObserver(SetObserver<E> observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    @Override
    public boolean removeObserver(SetObserver<E> observer) {
        synchronized (observers) {
            return observers.remove(observer);
        }
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
        synchronized (observers) {
            for (var observer : observers) {
                observer.added(this, element);
            }
        }
    }
}
