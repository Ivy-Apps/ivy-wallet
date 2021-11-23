package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

class ReorderModal<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun moveToTop(itemPosition: Int) {
        //TODO: RecyclerView reorder not working
        composeTestRule.onNode(
            hasTestTag("reorder_drag_handle")
                .and(hasContentDescription("reorder_${itemPosition}"))
        ).performGesture {
            swipeUp()
        }
    }

    fun clickDone() {
        composeTestRule.onNodeWithTag("reorder_done")
            .performClick()
    }
}