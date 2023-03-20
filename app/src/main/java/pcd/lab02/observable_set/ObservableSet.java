package pcd.lab02.observable_set;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ObservableSet<E> {

    private final Set<E> set;
    private final List<SetObserver<E>> observers;

    public ObservableSet(final Set<E> set) {
        this.set = set;
        this.observers = new LinkedList<>();
    }

    public void addObserver(final SetObserver<E> setObserver) {
        synchronized(observers) {
            observers.add(setObserver);
        }
    }

    public void removeObserver(final SetObserver<E> observer) {
        synchronized(observers) {
            observers.remove(observer);
        }
    }

    private void notifyElementAdded(final E element) {
        synchronized(observers) {
            for (final var observer: observers) {
                observer.added(this, element);
            }
        }
    }

    public boolean add(final E element) {
        final var added = set.add(element);
        if (added) {
            notifyElementAdded(element);
        }
        return added;
    }

    public boolean addAll(final Collection<? extends E> elements) {
        var result = false;
        for (E element: elements) {
            result = result || add(element);
        }
        return result;
    }
}
