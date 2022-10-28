package io.code_gems.cloud.synced_cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DisplayName("In-memory synced collection API behaviour:")
class SyncedCollectionTest {

    public static final String ITEM_1 = "item1";

    @SuppressWarnings({"ConstantConditions", "RedundantCollectionOperation"})
    @Test
    @DisplayName("behavior should be identical to unmodifiableCollection")
    void apiBehavior() {
        var unmodifiableCollection = Collections.unmodifiableCollection(Collections.singleton(ITEM_1));
        var syncSupplier = new MockSyncCollectionSupplier<>();
        var testedCollection = SyncedCollection.create(syncSupplier);
        syncSupplier.mockSupplyWith(Collections.singleton(ITEM_1));
        testedCollection.startSync();

        assertThat(testedCollection.contains(ITEM_1)).isEqualTo(unmodifiableCollection.contains(ITEM_1));
        assertThat(testedCollection.contains("")).isEqualTo(unmodifiableCollection.contains(""));
        assertThat(testedCollection).hasSameSizeAs(unmodifiableCollection);
        assertThat(testedCollection.isEmpty()).isEqualTo(unmodifiableCollection.isEmpty());
        assertThat(testedCollection.stream().anyMatch(a -> a.equals(ITEM_1)))
                .isEqualTo(unmodifiableCollection.stream().anyMatch(a -> a.equals(ITEM_1)));
        assertEqualFailure(() -> testedCollection.add(""), () -> unmodifiableCollection.add(""), "add");
        assertEqualFailure(() -> testedCollection.addAll(Collections.singleton("")),
                () -> unmodifiableCollection.addAll(Collections.singleton("")), "addAll");
        assertEqualFailure(() -> testedCollection.remove(""), () -> unmodifiableCollection.remove(""), "remove");
        assertEqualFailure(() -> testedCollection.removeAll(Collections.singleton("")),
                () -> unmodifiableCollection.removeAll(Collections.singleton("")), "removeAll");
        assertEqualFailure(testedCollection::clear, unmodifiableCollection::clear, "clear");
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