package io.code_gems.cloud.synced_cache;

import java.util.Collection;

@FunctionalInterface
public interface SyncCollectionSupplier<E> {

    Collection<E> get();

}
