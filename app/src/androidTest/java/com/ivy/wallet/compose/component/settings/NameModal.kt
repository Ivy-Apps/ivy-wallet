package com.ivy.wallet.compose.component.settings

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.ivy.wallet.compose.IvyComposeTestRule

class NameModal(
    private val composeTestRule: IvyComposeTestRule
) {

    fun enterName(name: String): NameModal {
        composeTestRule.onNodeWithTag("input_field")
            .performTextReplacement(name)
        return this
    }

    fun clickSave(): SettingsScreen {
        composeTestRule.onNodeWithText("Save")
            .performClick()
        return SettingsScreen(composeTestRule)
    }
}