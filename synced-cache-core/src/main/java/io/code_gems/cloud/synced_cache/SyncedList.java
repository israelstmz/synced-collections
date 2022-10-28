package io.code_gems.cloud.synced_cache;

import java.util.List;

/**
 * A <b>read-only</b> Map, synced with a backing one.
 * Mainly intended as a fast, in-memory 'cache' layer to a remote, persistent and centralized List.
 * <p>
 *      <b>Important: </b> any attempt to mutate or modify the Map will throw an {@link UnsupportedOperationException}.
 * </p>
 * @param <E> the type of the List elements
 */
public interface SyncedList<E> extends SyncedCollection<E>, List<E> {
}
