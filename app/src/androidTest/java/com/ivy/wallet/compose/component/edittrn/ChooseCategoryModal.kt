package com.ivy.wallet.compose.component.edittrn

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule

class ChooseCategoryModal(
    private val composeTestRule: IvyComposeTestRule
) {

    fun <N> selectCategory(categoryName: String, next: N): N {
        composeTestRule.onNode(
            hasTestTag("choose_category_button").and(hasText(categoryName))
        ).performClick()
        return next
    }

    fun <N> skip(next: N): N {
        composeTestRule.onNodeWithText("Skip")
            .performClick()
        return next
    }
}