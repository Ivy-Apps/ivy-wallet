package com.ivy.wallet.compose.component.picker

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.ivy.wallet.compose.IvyComposeTestRule

class IvyIconPicker(
    private val composeTestRule: IvyComposeTestRule
) {
    fun chooseIcon(icon: String) {
        composeTestRule.onNodeWithTag("modal_item_icon")
            .performClick()

        composeTestRule.onNodeWithTag(icon)
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithTag("choose_icon_save")
            .performClick()
    }
}

interface IconPicker<T> {
    fun chooseIcon(icon: String): T
}