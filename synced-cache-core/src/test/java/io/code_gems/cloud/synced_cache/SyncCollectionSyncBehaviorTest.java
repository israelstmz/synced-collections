package io.code_gems.cloud.synced_cache;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SuppressWarnings({"ResultOfMethodCallIgnored", "RedundantCollectionOperation"})
@DisplayName("Sync behavior:")
class SyncCollectionSyncBehaviorTest {

    private static final String ITEM_1 = "item-1";
    public static final Duration INTERVAL = Duration.ofMillis(1);

    private MockSyncCollectionSupplier<String> mockSupplier;
    private SyncedCollection<String> testedCollection;

    @BeforeEach
    void setUp() {
        mockSupplier = new MockSyncCollectionSupplier<>();
        testedCollection = SyncedCollection.build(mockSupplier)
                .interval(INTERVAL)
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

    @SuppressWarnings("unused")
    @ParameterizedTest(name = "''{1}''")
    @DisplayName("in case no sync ever succeeded - following accessor actions should fail:")
    @MethodSource
    void noSyncEverSucceeded(ThrowableAssert.ThrowingCallable action, String actionName) {
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                assertThatThrownBy(action).isInstanceOf(OutOfSyncException.class));
    }

    static Stream<Arguments> noSyncEverSucceeded() {
        var mockSupplier = new MockSyncCollectionSupplier<>();
        mockSupplier.mockSupplyFailure();
        var testedCollection = SyncedCollection.build(mockSupplier).interval(Duration.ofMillis(1)).build();
        testedCollection.startSync();
        return Stream.of(
                arguments((ThrowableAssert.ThrowingCallable) () -> testedCollection.contains(ITEM_1), "contains"),
                arguments((ThrowableAssert.ThrowingCallable) () -> testedCollection.containsAll(Collections.singleton(ITEM_1)), "containsAll"),
                arguments((ThrowableAssert.ThrowingCallable) () -> testedCollection.forEach(item -> {}), "forEach"),
                arguments((ThrowableAssert.ThrowingCallable) testedCollection::size, "size"),
                arguments((ThrowableAssert.ThrowingCallable) testedCollection::iterator, "iterator"),
                arguments((ThrowableAssert.ThrowingCallable) testedCollection::stream, "stream"),
                arguments((ThrowableAssert.ThrowingCallable) testedCollection::spliterator, "spliterator")
        );
    }

    @SuppressWarnings("unused")
    @ParameterizedTest(name = "''{1}''")
    @DisplayName("in case no sync succeeded during allowed period - following accessor actions should fail:")
    @MethodSource
    void outOfSync(ThrowableAssert.ThrowingCallable action, String actionName) {
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                assertThatThrownBy(action).isInstanceOf(OutOfSyncException.class));
    }

    static Stream<Arguments> outOfSync() {
        var mockSupplier = new MockSyncCollectionSupplier<>();
        mockSupplier.mockSupplyFailure();
        var testedCollection = SyncedCollection.build(mockSupplier)
                                               .interval(Duration.ofMillis(1))
                                               .initialCollection(Collections.emptyList())
                                               .build();
        testedCollection.startSync();
        return Stream.of(
                arguments((ThrowableAssert.ThrowingCallable) () -> testedCollection.contains(ITEM_1), "contains"),
                arguments((ThrowableAssert.ThrowingCallable) () -> testedCollection.containsAll(Collections.singleton(ITEM_1)), "containsAll"),
                arguments((ThrowableAssert.ThrowingCallable) () -> testedCollection.forEach(item -> {}), "forEach"),
                arguments((ThrowableAssert.ThrowingCallable) testedCollection::size, "size"),
                arguments((ThrowableAssert.ThrowingCallable) testedCollection::iterator, "iterator"),
                arguments((ThrowableAssert.ThrowingCallable) testedCollection::stream, "stream"),
                arguments((ThrowableAssert.ThrowingCallable) testedCollection::spliterator, "spliterator")
        );
    }

}
