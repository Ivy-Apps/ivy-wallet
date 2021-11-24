package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.compose.waitMillis

class OnboardingFlow<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val homeTab = HomeTab(composeTestRule)

    fun chooseOfflineAccount(retryAttempt: Int = 0) {
        try {
            composeTestRule.waitForIdle()
            composeTestRule.onNode(hasText("Offline account"))
                .assertExists()
                .performClick()
        } catch (e: AssertionError) {
            composeTestRule.waitMillis(300)

            if (retryAttempt < 5) {
                chooseOfflineAccount(retryAttempt = retryAttempt + 1)
            }
        }
    }

    fun clickStartFresh() {
        composeTestRule.onNode(hasText("Start fresh"))
            .performClick()
    }

    fun setCurrency() {
        composeTestRule.onNode(hasText("Set"))
            .performClick()
    }

    fun skipAccounts() {
        composeTestRule.onNode(hasText("Skip"))
            .performClick()
    }

    fun skipCategories() {
        composeTestRule.onNode(hasText("Skip"))
            .performClick()
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