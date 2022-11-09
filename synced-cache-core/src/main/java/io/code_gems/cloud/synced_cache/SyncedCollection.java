package io.code_gems.cloud.synced_cache;

import java.time.Duration;
import java.util.Collection;

/**
 * A <b>read-only</b> Collection, synced with a backing one.
 * Mainly intended as a fast, in-memory 'cache' layer to a remote, persistent and centralized collection.
 * <p>
 *      <b>Important: </b> any attempt to mutate or modify the Collection will throw an {@link UnsupportedOperationException}.
 *      {@link #startSync()} must be called once, post instantiation, in order to start the sync process.
 * </p>
 * @param <E> the type of the Collection elements
 */
public interface SyncedCollection<E> extends Collection<E> {

    void startSync();
    boolean isSynced();

    static <E> SyncedCollection<E> create(SyncCollectionSupplier<E> syncCollectionSupplier) {
        return StrictSyncedCollection.<E>builder()
                .syncCollectionSupplier(syncCollectionSupplier)
                .build();
    }

    static <E> SyncedCollectionBuilder<E> build(SyncCollectionSupplier<E> syncCollectionSupplier) {
        return new SyncedCollectionBuilder<>(syncCollectionSupplier);
    }

    class SyncedCollectionBuilder<E> {

        private final SyncCollectionSupplier<E> syncCollectionSupplier;
        private Collection<E> initialCollection;
        private Duration interval;
        private Integer maxAllowedNoSyncIntervals;

        public SyncedCollectionBuilder(SyncCollectionSupplier<E> syncCollectionSupplier) {
            this.syncCollectionSupplier = syncCollectionSupplier;
        }

        public SyncedCollectionBuilder<E> initialCollection(Collection<E> initialCollection) {
            this.initialCollection = initialCollection;
            return this;
        }

        public SyncedCollectionBuilder<E> interval(Duration interval) {
            this.interval = interval;
            return this;
        }

        public SyncedCollectionBuilder<E> maxAllowedNoSyncIntervals(Integer intervals) {
            this.maxAllowedNoSyncIntervals = intervals;
            return this;
        }

        public SyncedCollection<E> build() {
            return StrictSyncedCollection.<E>builder()
                    .syncCollectionSupplier(syncCollectionSupplier)
                    .interval(interval)
                    .initialCollection(initialCollection)
                    .maxAllowedNoSyncIntervals(maxAllowedNoSyncIntervals)
                    .build();
        }

    }

}
