package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.compose.clickWithRetry

class OnboardingFlow<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val homeTab = HomeTab(composeTestRule)

    fun chooseOfflineAccount() {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("Offline account")),
            maxRetries = 10
        )
    }

    fun clickStartFresh() {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("Start fresh"))
        )
    }

    fun setCurrency() {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("Set"))
        )
    }

    fun skipAccounts() {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("Skip"))
        )
    }

    fun skipCategories() {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("Skip"))
        )
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