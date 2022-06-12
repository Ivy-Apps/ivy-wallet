package com.ivy.wallet.compose.component.settings

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.DonateScreen
import com.ivy.wallet.compose.component.home.HomeMoreMenu

class SettingsScreen(
    private val composeTestRule: IvyComposeTestRule
) {

    fun clickLockApp(): SettingsScreen {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Lock app")
            .performScrollTo()
            .performClick()
        return this
    }

    fun clickProfileCard(): NameModal {
        composeTestRule.onNodeWithTag("settings_profile_card", useUnmergedTree = true)
            .performClick()
        return NameModal(composeTestRule)
    }

    fun assertLocalAccountName(
        name: String
    ): SettingsScreen {
        composeTestRule.onNodeWithTag("local_account_name", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(name)
        return this
    }

    fun clickBack(): HomeMoreMenu {
        composeTestRule.onNodeWithTag("toolbar_back")
            .performClick()
        return HomeMoreMenu(composeTestRule)
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