package io.code_gems.cloud.synced_cache;

import lombok.extern.java.Log;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
@Log
class InMemSyncedCollection<E> implements SyncedCollection<E> {

    private static final int NO_DELAY = 0;
    private static final Duration DEFAULT_INTERVAL = Duration.ofMinutes(1);
    private static final int DEFAULT_MAX_ALLOWED_NO_SYNC_INTERVALS = 4;
    private static final Supplier<ScheduledExecutorService> DEFAULT_SCHEDULER_SUPPLIER =
            () -> Executors.newScheduledThreadPool(1);

    private final SyncCollectionSupplier<E> syncCollectionSupplier;
    private final ScheduledExecutorService scheduler;
    private final Duration interval;
    private final AtomicInteger noSyncIntervals;
    private final int maxAllowedNoSyncIntervals;
    private boolean isSynced;
    private Collection<E> collection;

    InMemSyncedCollection(SyncCollectionSupplier<E> syncCollectionSupplier, Duration interval,
                          Collection<E> initialCollection, ScheduledExecutorService scheduler,
                          Integer maxAllowedNoSyncIntervals) {
        if (syncCollectionSupplier == null) {
            throw new IllegalStateException("Instance of SyncCollectionSupplier must be provided");
        }
        this.syncCollectionSupplier = syncCollectionSupplier;
        this.maxAllowedNoSyncIntervals = Optional.ofNullable(maxAllowedNoSyncIntervals).orElse(DEFAULT_MAX_ALLOWED_NO_SYNC_INTERVALS);
        this.scheduler = Optional.ofNullable(scheduler).orElseGet(DEFAULT_SCHEDULER_SUPPLIER);
        this.interval = Optional.ofNullable(interval).orElse(DEFAULT_INTERVAL);
        this.noSyncIntervals = new AtomicInteger(0);
        this.collection = Collections.unmodifiableCollection(
                Optional.ofNullable(initialCollection).orElse(Collections.emptyList()));
        if (initialCollection != null) {
            isSynced = true;
        }
    }

    public void startSync() {
        scheduler.scheduleWithFixedDelay(this::syncWithSupplier, NO_DELAY, interval.toMillis(), TimeUnit.MILLISECONDS);
    }

    public boolean isSynced() {
        return isSynced;
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

    private void syncWithSupplier() {
        try {
            this.collection = Collections.unmodifiableCollection(syncCollectionSupplier.get());
            isSynced = true;
            noSyncIntervals.set(0);
        } catch (Exception e) {
            checkAllowedNoSyncPeriod();
            log.warning("sync failed: " + e);
        }
    }

    private void checkAllowedNoSyncPeriod() {
        if (noSyncIntervals.incrementAndGet() > maxAllowedNoSyncIntervals) {
            isSynced = false;
        }
    }

}
