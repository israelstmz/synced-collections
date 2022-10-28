package io.code_gems.cloud.synced_cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

class InMemSyncedCollection<E> implements SyncedCollection<E> {

    private final SyncCollectionSupplier<E> syncCollectionSupplier;
    private Collection<E> collection;

    public InMemSyncedCollection(SyncCollectionSupplier<E> syncCollectionSupplier) {
        this.syncCollectionSupplier = syncCollectionSupplier;
    }

    public void startSync() {
        collection = Collections.unmodifiableCollection(syncCollectionSupplier.get());
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean contains(Object o) {
        return collection.contains(o);
    }

    public Iterator<E> iterator() {
        return null;
    }

    public Object[] toArray() {
        return new Object[0];
    }

    public <T> T[] toArray(T[] a) {
        return null;
    }

    public boolean add(E e) {
        return collection.add(e);
    }

    public boolean remove(Object o) {
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        return false;
    }

    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    public boolean removeAll(Collection<?> c) {
        return false;
    }

    public boolean retainAll(Collection<?> c) {
        return false;
    }

    public void clear() {

    }
}
