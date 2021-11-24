package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.rules.ActivityScenarioRule

class NameModal<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
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