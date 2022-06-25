package com.ivy.wallet.compose.component

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule

class ReorderModal(
    private val composeTestRule: IvyComposeTestRule
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