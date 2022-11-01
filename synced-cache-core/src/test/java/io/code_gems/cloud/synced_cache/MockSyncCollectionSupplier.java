package io.code_gems.cloud.synced_cache;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

class MockSyncCollectionSupplier<E> implements SyncCollectionSupplier<E> {

    private Supplier<Collection<E>> collectionSupplier;

    public MockSyncCollectionSupplier() {
        collectionSupplier = Collections::emptyList;
    }

    public void mockSupplyWith(Collection<E> collection) {
        collectionSupplier = () -> collection;
    }

    public void mockSupplyFailure() {
        collectionSupplier = () -> {
            throw new RuntimeException();
        };
    }

    @Override
    public Collection<E> get() {
        return collectionSupplier.get();
    }
}
