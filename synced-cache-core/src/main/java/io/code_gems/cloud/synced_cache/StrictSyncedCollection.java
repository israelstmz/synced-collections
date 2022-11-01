package io.code_gems.cloud.synced_cache;

import lombok.AccessLevel;
import lombok.Builder;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

public class StrictSyncedCollection<E> extends InMemSyncedCollection<E> {

    @Builder(access = AccessLevel.PACKAGE)
    StrictSyncedCollection(SyncCollectionSupplier<E> syncCollectionSupplier, Duration interval,
                           Collection<E> initialCollection, ScheduledExecutorService scheduler) {
        super(syncCollectionSupplier, interval, initialCollection, scheduler);
    }

    @Override
    public boolean contains(Object o) {
        if (!isSynced()) {
            throw new OutOfSyncException();
        }
        return super.contains(o);
    }
}
