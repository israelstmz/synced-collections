package io.code_gems.cloud.synced_cache;

import java.util.Collection;

class MockSyncCollectionSupplier<E> implements SyncCollectionSupplier<E> {

    private Collection<E> collection;

    public void mockSupplyWith(Collection<E> collection) {
        this.collection = collection;
    }

    @Override
    public Collection<E> get() {
        return collection;
    }
}
