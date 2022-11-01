package io.code_gems.cloud.synced_cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

@DisplayName("Sync behavior:")
class SyncCollectionSyncBehaviorTest {

    private static final String ITEM_1 = "item-1";

    private MockSyncCollectionSupplier<String> mockSupplier;
    private SyncedCollection<String> testedCollection;

    @BeforeEach
    void setUp() {
        mockSupplier = new MockSyncCollectionSupplier<>();
        testedCollection = SyncedCollection.build(mockSupplier)
                .interval(Duration.ofMillis(1))
                .build();
        testedCollection.startSync();
    }

    @Test
    @DisplayName("a new item added through sync - should appear in collection")
    void itemAddedInSubsequentSync() {
        mockSupplier.mockSupplyWith(Collections.emptyList());
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(testedCollection.contains(ITEM_1)).isFalse());

        mockSupplier.mockSupplyWith(Collections.singleton(ITEM_1));
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(testedCollection.contains(ITEM_1)).isTrue());
    }

    @Test
    @DisplayName("an item removed through sync - should not appear in collection")
    void itemRemovedInSubsequentSync() {
        mockSupplier.mockSupplyWith(Collections.singleton(ITEM_1));
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(testedCollection.contains(ITEM_1)).isTrue());

        mockSupplier.mockSupplyWith(Collections.emptyList());
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(testedCollection.contains(ITEM_1)).isFalse());
    }

    @Test
    @DisplayName("in case supplier failed - scheduler should keep executing next syncs")
    void supplierFailureShouldNotStopSync() {
        mockSupplier.mockSupplyFailure();
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(testedCollection).isEmpty());

        mockSupplier.mockSupplyWith(Collections.singleton(ITEM_1));
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(testedCollection.contains(ITEM_1)).isTrue());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Nested
    @DisplayName("in case no sync ever succeeded:")
    class NoSyncEverSucceeded {

        @BeforeEach
        void setUp() {
            mockSupplier = new MockSyncCollectionSupplier<>();
            mockSupplier.mockSupplyFailure();
            testedCollection = SyncedCollection.build(mockSupplier)
                                               .interval(Duration.ofMillis(1))
                                               .build();
            testedCollection.startSync();
        }

        @Test
        @DisplayName("'contains' action should fail with OutOfSyncException")
        void contains() {
            await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                    assertThatThrownBy(() -> testedCollection.contains(ITEM_1))
                            .isInstanceOf(OutOfSyncException.class));
        }

        @Test
        @DisplayName("'containsAll' action should fail with OutOfSyncException")
        void containsAll() {
            var otherCollection = Collections.singleton(ITEM_1);
            await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                    assertThatThrownBy(() -> testedCollection.containsAll(otherCollection))
                            .isInstanceOf(OutOfSyncException.class));
        }

    }

}
