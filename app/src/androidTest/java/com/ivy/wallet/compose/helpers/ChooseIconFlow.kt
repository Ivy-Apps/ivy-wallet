package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.rules.ActivityScenarioRule

class ChooseIconFlow<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun chooseIcon(icon: String) {
        composeTestRule.onNodeWithTag("modal_item_icon")
            .performClick()

        composeTestRule.onNodeWithTag(icon)
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithText("Save")
            .performClick()
    }
}