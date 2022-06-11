package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.ivy.wallet.compose.IvyComposeTestRule

class NameModal(
    private val composeTestRule: IvyComposeTestRule
) {

    fun enterName(name: String) {
        composeTestRule.onNodeWithTag("input_field")
            .performTextReplacement(name)
    }

    fun clickSave() {
        composeTestRule.onNodeWithText("Save")
            .performClick()
    }
}