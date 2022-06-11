package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.clickWithRetry

class OnboardingFlow(
    private val composeTestRule: IvyComposeTestRule
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

    fun quickOnboarding(): HomeTab {
        chooseOfflineAccount()
        clickStartFresh()
        setCurrency()
        skipAccounts()
        skipCategories()
        return HomeTab(composeTestRule)
    }

    fun onboardWith1AccountAnd1Category(): HomeTab {
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

        return homeTab
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