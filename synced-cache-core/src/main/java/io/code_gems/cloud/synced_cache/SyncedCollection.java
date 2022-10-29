package io.code_gems.cloud.synced_cache;

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

    static <E> SyncedCollection<E> create(SyncCollectionSupplier<E> syncCollectionSupplier) {
        return new InMemSyncedCollection<>(syncCollectionSupplier);
    }

    static <E> SyncedCollection<E> create(SyncCollectionSupplier<E> syncCollectionSupplier,
                                          Collection<E> initialCollection) {
        return new InMemSyncedCollection<>(syncCollectionSupplier, initialCollection);
    }

}
