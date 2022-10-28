package io.code_gems.cloud.synced_cache;

import java.util.Collection;

public interface SyncCollectionSupplier<E> {

    Collection<E> get();

}
