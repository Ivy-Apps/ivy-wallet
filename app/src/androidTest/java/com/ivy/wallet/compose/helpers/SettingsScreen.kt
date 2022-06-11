package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule

class SettingsScreen(
    private val composeTestRule: IvyComposeTestRule
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

    fun clickStartDateOfMonth() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Start date of month")
            .performScrollTo()
            .performClick()
    }

    fun clickDonate(): DonateScreen {
        composeTestRule.onNodeWithTag("settings_lazy_column")
            .performScrollToIndex(4)

        composeTestRule.onNodeWithText("Donate")
            .performScrollTo()
            .performClick()
        return DonateScreen(composeTestRule)
    }
}