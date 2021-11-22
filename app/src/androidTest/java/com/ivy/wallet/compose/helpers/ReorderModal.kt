package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.swipeUp
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
}