package io.code_gems.cloud.synced_cache;

import java.util.Map;

/**
 * A <b>read-only</b> Set, synced with a backing one.
 * Mainly intended as a fast, in-memory 'cache' layer to a remote, persistent and centralized Set.
 * <p>
 *      <b>Important: </b> any attempt to mutate or modify the Set will throw an {@link UnsupportedOperationException}.
 *      {@link #startSync()} must be called once, post instantiation in order to start the sync process.
 * </p>
 * @param <K> the type of the Map keys
 * @param <V> the type of the Map values
 */
public interface SyncedMap<K, V> extends Map<K, V> {

    void startSync();

}
