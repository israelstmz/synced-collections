package io.code_gems.cloud.synced_cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class SyncCollectionSyncBehaviorTest {

    private static final String ITEM_1 = "item-1";

    private MockSyncCollectionSupplier<String> mockSupplier;
    private SyncedCollection<String> testedCollection;

    @BeforeEach
    void setUp() {
        mockSupplier = new MockSyncCollectionSupplier<>();
        testedCollection = SyncedCollection.create(mockSupplier);
        testedCollection.startSync();
    }

    @Test
    @DisplayName("a new item in a subsequent sync should appear in collection")
    void itemAddedInSubsequentSync() {
        mockSupplier.mockSupplyWith(Collections.emptyList());
        await().atMost(Duration.ofSeconds(2)).ignoreExceptions().untilAsserted(() -> assertThat(testedCollection).doesNotContain(ITEM_1));

        mockSupplier.mockSupplyWith(Collections.singleton(ITEM_1));
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(testedCollection).contains(ITEM_1));
    }
}
