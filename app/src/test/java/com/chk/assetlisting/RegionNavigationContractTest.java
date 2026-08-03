package com.chk.assetlisting;

import static org.junit.Assert.assertEquals;

import com.chk.assetlisting.ui.RegionAssetsActivity;
import org.junit.Test;

public class RegionNavigationContractTest {
    @Test
    public void regionScreenUsesExpectedIntentKeys() {
        assertEquals("game_id", RegionAssetsActivity.EXTRA_GAME_ID);
        assertEquals("region_id", RegionAssetsActivity.EXTRA_REGION_ID);
    }
}
