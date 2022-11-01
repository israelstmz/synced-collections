package io.code_gems.cloud.synced_cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SuppressWarnings({"ConstantConditions", "RedundantCollectionOperation"})
@DisplayName("Synced-Collection API should behave like 'Collection.unmodifiableCollection()':")
class SyncedCollectionCommonApiTest {

    public static final String ITEM_1 = "item1";
    public static final String ITEM_2 = "item2";

    @Test
    @DisplayName("'contains' behavior")
    void contains() {
        var unmodifiableCollection = createUnmodifiableCollectionOf(Collections.singleton(ITEM_1));
        var testedCollection = createTestedCollectionOf(Collections.singleton(ITEM_1));

        assertThat(testedCollection.contains(ITEM_1)).isEqualTo(unmodifiableCollection.contains(ITEM_1));
        assertThat(testedCollection.contains(ITEM_2)).isEqualTo(unmodifiableCollection.contains(ITEM_2));
        assertThat(testedCollection.containsAll(Collections.singleton(ITEM_1)))
                .isEqualTo(unmodifiableCollection.containsAll(Collections.singleton(ITEM_1)));
        assertThat(testedCollection.containsAll(Collections.singleton(ITEM_2)))
                .isEqualTo(unmodifiableCollection.containsAll(Collections.singleton(ITEM_2)));
    }

    @Test
    @DisplayName("'size' behavior")
    void size() {
        var unmodifiableCollection = createUnmodifiableCollectionOf(Collections.singleton(ITEM_1));
        var testedCollection = createTestedCollectionOf(Collections.singleton(ITEM_1));

        assertThat(testedCollection).hasSameSizeAs(unmodifiableCollection);
    }

    @Test
    @DisplayName("'isEmpty' behavior")
    void isEmpty() {
        var unmodifiableCollection = createUnmodifiableCollectionOf(Collections.emptyList());
        var testedCollection = createTestedCollectionOf(Collections.emptyList());

        assertThat(testedCollection.isEmpty()).isEqualTo(unmodifiableCollection.isEmpty());

        unmodifiableCollection = createUnmodifiableCollectionOf(Collections.singleton(ITEM_1));
        testedCollection = createTestedCollectionOf(Collections.singleton(ITEM_1));

        assertThat(testedCollection.isEmpty()).isEqualTo(unmodifiableCollection.isEmpty());
    }

    @Test
    @DisplayName("'stream' behavior")
    void stream() {
        var unmodifiableCollection = createUnmodifiableCollectionOf(Collections.singleton(ITEM_1));
        var testedCollection = createTestedCollectionOf(Collections.singleton(ITEM_1));

        assertThat(testedCollection.stream().anyMatch(a -> a.equals(ITEM_1)))
                .isEqualTo(unmodifiableCollection.stream().anyMatch(a -> a.equals(ITEM_1)));
        assertThat(testedCollection.stream().anyMatch(a -> a.equals(ITEM_2)))
                .isEqualTo(unmodifiableCollection.stream().anyMatch(a -> a.equals(ITEM_2)));
    }

    @Test
    @DisplayName("'add' behavior")
    void add() {
        var unmodifiableCollection = createUnmodifiableCollectionOf(Collections.singleton(ITEM_1));
        var testedCollection = createTestedCollectionOf(Collections.singleton(ITEM_1));

        assertEqualFailure(() -> testedCollection.add(ITEM_2), () -> unmodifiableCollection.add(ITEM_2), "add");
        assertEqualFailure(() -> testedCollection.addAll(Collections.singleton(ITEM_2)),
                () -> unmodifiableCollection.addAll(Collections.singleton(ITEM_2)), "addAll");
    }

    @Test
    @DisplayName("'remove' behavior")
    void remove() {
        var unmodifiableCollection = createUnmodifiableCollectionOf(Collections.singleton(ITEM_1));
        var testedCollection = createTestedCollectionOf(Collections.singleton(ITEM_1));

        assertEqualFailure(() -> testedCollection.remove(ITEM_1), () -> unmodifiableCollection.remove(ITEM_1), "remove");
        assertEqualFailure(() -> testedCollection.removeAll(Collections.singleton(ITEM_1)),
                () -> unmodifiableCollection.removeAll(Collections.singleton(ITEM_1)), "removeAll");
    }

    @Test
    @DisplayName("'clear' behavior")
    void clear() {
        var unmodifiableCollection = createUnmodifiableCollectionOf(Collections.singleton(ITEM_1));
        var testedCollection = createTestedCollectionOf(Collections.singleton(ITEM_1));

        assertEqualFailure(testedCollection::clear, unmodifiableCollection::clear, "clear");

    }

    private static Collection<String> createUnmodifiableCollectionOf(Collection<String> collection) {
        return Collections.unmodifiableCollection(collection);
    }

    private SyncedCollection<String> createTestedCollectionOf(Collection<String> collection) {
        var syncSupplier = new MockSyncCollectionSupplier<String>();
        var testedCollection = SyncedCollection.build(syncSupplier)
                                                       .initialCollection(collection)
                                                       .interval(Duration.ofMillis(1))
                                                       .build();
        syncSupplier.mockSupplyWith(collection);
        testedCollection.startSync();
        return testedCollection;
    }

    private void assertEqualFailure(Runnable testedAction, Runnable expectedAction, String actionName) {
        try {
            testedAction.run();
            fail("tested action did not fail: " + actionName);
        } catch (Exception actual) {
            try {
                expectedAction.run();
                fail("expected action did not fail: " + actionName);
            } catch (Exception expected) {
                assertThat(actual).isInstanceOf(expected.getClass());
            }
        }
    }
}