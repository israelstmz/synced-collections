package io.code_gems.cloud.synced_cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A <b>read-only</b> Collection, synced with a backing one.
 * Mainly intended as a fast, in-memory 'cache' layer to a remote, persistent and centralized collection.
 * <p>
 *      <b>Important: </b> any attempt to mutate or modify the Collection will throw an {@link UnsupportedOperationException}.
 *      {@link #startSync()} must be called once, post instantiation, in order to start the sync process.
 * </p>
 * @param <E> the type of the Collection elements
 */
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
        return collection.size();
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public boolean contains(Object o) {
        return collection.contains(o);
    }

    public Iterator<E> iterator() {
        return collection.iterator();
    }

    public Object[] toArray() {
        return collection.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return collection.toArray(a);
    }

    public boolean add(E e) {
        return collection.add(e);
    }

    public boolean remove(Object o) {
        return collection.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return collection.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return collection.addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return collection.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return collection.retainAll(c);
    }

    public void clear() {
        collection.clear();
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return collection.toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return collection.removeIf(filter);
    }

    @Override
    public Spliterator<E> spliterator() {
        return collection.spliterator();
    }

    @Override
    public Stream<E> stream() {
        return collection.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return collection.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        collection.forEach(action);
    }

    @Override
    public int hashCode() {
        return collection.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return collection.equals(obj);
    }

    @Override
    public String toString() {
        return collection.toString();
    }

}
