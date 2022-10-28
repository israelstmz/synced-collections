package io.code_gems.cloud.synced_cache;

import java.util.Set;

/**
 * A <b>read-only</b> Set, synced with a backing one.
 * Mainly intended as a fast, in-memory 'cache' layer to a remote, persistent and centralized Set.
 * <p>
 *      <b>Important: </b> any attempt to mutate or modify the Set will throw an {@link UnsupportedOperationException}.
 * </p>
 * @param <E> the type of the Set elements
 */
public interface SyncedSet<E> extends SyncedCollection<E>, Set<E> {
}
