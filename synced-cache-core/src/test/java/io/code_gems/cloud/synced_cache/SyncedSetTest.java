package io.code_gems.cloud.synced_cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DisplayName("In-memory synced set API behaviour:")
class SyncedSetTest {

    public static final String ITEM_1 = "item1";

    @Test
    @DisplayName("behavior should be identical to unmodifiableCollection")
    void apiBehavior() {
        var unmodifiableCollection = Collections.unmodifiableCollection(Collections.singleton(ITEM_1));
        var syncSupplier = new MockSyncCollectionSupplier<>();
        var testedSet = SyncedCollection.create(syncSupplier);
        syncSupplier.mockSupplyWith(Collections.singleton(ITEM_1));
        testedSet.startSync();

        assertThat(testedSet.contains(ITEM_1)).isEqualTo(unmodifiableCollection.contains(ITEM_1));
        assertEqualFailure(() -> testedSet.add(""), () -> unmodifiableCollection.add(""), "add");
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