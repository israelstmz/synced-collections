package io.code_gems.cloud.synced_cache;

import java.util.Collection;

/**
 * A <b>read-only</b> Collection, synced with a backing one.
 * Mainly intended as a fast, in-memory 'cache' layer to a remote, persistent and centralized collection.
 * <p>
 *      <b>Important: </b> any attempt to mutate or modify the Collection will throw an {@link UnsupportedOperationException}.
 * </p>
 * @param <E> the type of the Collection elements
 */
public interface SyncedCollection<E> extends Collection<E> {

    void startSync();

}
