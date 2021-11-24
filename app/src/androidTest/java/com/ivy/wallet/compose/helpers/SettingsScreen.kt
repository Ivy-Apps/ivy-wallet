package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

class SettingsScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun clickLockApp() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Lock app")
            .performScrollTo()
            .performClick()
    }

    fun clickProfileCard() {
        composeTestRule.onNodeWithTag("settings_profile_card", useUnmergedTree = true)
            .performClick()
    }

    fun assertLocalAccountName(
        name: String
    ) {
        composeTestRule.onNodeWithTag("local_account_name", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(name)
    }

    fun clickBack() {
        composeTestRule.onNodeWithTag("toolbar_back")
            .performClick()
    }
}