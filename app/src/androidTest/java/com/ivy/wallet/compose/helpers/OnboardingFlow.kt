package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.compose.waitSeconds

class OnboardingFlow<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val homeTab = HomeTab(composeTestRule)

    fun chooseOfflineAccount() {
        //Wait 1 second to make sure the app state is stable
        composeTestRule.waitSeconds(1)
        composeTestRule.waitForIdle()

        composeTestRule.onNode(hasText("Offline account"))
            .performClick()

        composeTestRule.onNode(hasText("Import CSV file"))
            .assertIsDisplayed()
    }

    fun clickStartFresh() {
        composeTestRule.onNode(hasText("Start fresh"))
            .performClick()

        composeTestRule.onNode(hasText("Set currency"))
            .assertIsDisplayed()
    }

    fun setCurrency() {
        composeTestRule.onNode(hasText("Set"))
            .performClick()

        composeTestRule.onNode(hasText("Add accounts"))
            .assertIsDisplayed()
    }

    fun skipAccounts() {
        composeTestRule.onNode(hasText("Skip"))
            .performClick()

        composeTestRule.onNode(hasText("Add categories"))
            .assertIsDisplayed()
    }

    fun skipCategories() {
        composeTestRule.onNode(hasText("Skip"))
            .performClick()

        composeTestRule.onNode(hasText("Hi"))
            .assertIsDisplayed()
    }

    fun quickOnboarding() {
        chooseOfflineAccount()
        clickStartFresh()
        setCurrency()
        skipAccounts()
        skipCategories()
    }

    fun onboardWith1AccountAnd1Category() {
        chooseOfflineAccount()
        clickStartFresh()
        setCurrency()

        clickItemSuggestion("Cash")
        clickAccountsNext()

        clickItemSuggestion("Food & Drinks")
        clickCategoriesFinish()

        homeTab.assertBalance(
            amount = "0",
            amountDecimal = ".00"
        )
    }


    private fun clickItemSuggestion(suggestion: String) {
        composeTestRule.onNodeWithText(suggestion)
            .performClick()
    }

    private fun clickAccountsNext() {
        composeTestRule.onNodeWithText("Next")
            .performClick()
    }

    private fun clickCategoriesFinish() {
        composeTestRule.onNodeWithText("Finish")
            .performClick()
    }
}