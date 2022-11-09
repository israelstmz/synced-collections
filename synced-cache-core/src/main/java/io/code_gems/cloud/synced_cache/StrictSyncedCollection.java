package io.code_gems.cloud.synced_cache;

import lombok.AccessLevel;
import lombok.Builder;

import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StrictSyncedCollection<E> extends InMemSyncedCollection<E> {

    @Builder(access = AccessLevel.PACKAGE)
    StrictSyncedCollection(SyncCollectionSupplier<E> syncCollectionSupplier, Duration interval,
                           Collection<E> initialCollection, ScheduledExecutorService scheduler,
                           Integer maxAllowedNoSyncIntervals) {
        super(syncCollectionSupplier, interval, initialCollection, scheduler, maxAllowedNoSyncIntervals);
    }

    @Override
    public boolean contains(Object o) {
        verifyCollectionIsSynced();
        return super.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        verifyCollectionIsSynced();
        return super.containsAll(c);
    }

    @Override
    public int size() {
        verifyCollectionIsSynced();
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        verifyCollectionIsSynced();
        return super.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        verifyCollectionIsSynced();
        return super.iterator();
    }

    @Override
    public Stream<E> stream() {
        verifyCollectionIsSynced();
        return super.stream();
    }

    @Override
    public Spliterator<E> spliterator() {
        verifyCollectionIsSynced();
        return super.spliterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        verifyCollectionIsSynced();
        super.forEach(action);
    }

    private void verifyCollectionIsSynced() {
        if (!isSynced()) {
            throw new OutOfSyncException();
        }
    }
}
