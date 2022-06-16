package com.ivy.wallet.compose.component

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.clickWithRetry
import com.ivy.wallet.compose.component.home.HomeTab

class OnboardingFlow(
    private val composeTestRule: IvyComposeTestRule
) {
    private val homeTab = HomeTab(composeTestRule)

    fun chooseOfflineAccount(): OnboardingFlow {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("Offline account")),
            maxRetries = 10
        )
        return this
    }

    fun clickStartFresh(): OnboardingFlow {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("Start fresh"))
        )
        return this
    }

    fun setCurrency(): OnboardingFlow {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("Set"))
        )
        return this
    }

    fun skipAccounts(): OnboardingFlow {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("Skip"))
        )
        return this
    }

    fun skipCategories(): HomeTab {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("Skip"))
        )
        return HomeTab(composeTestRule)
    }

    fun quickOnboarding(): HomeTab {
        return chooseOfflineAccount()
            .clickStartFresh()
            .setCurrency()
            .skipAccounts()
            .skipCategories()
    }

    fun onboardWith1AccountAnd1Category(): HomeTab {
        return chooseOfflineAccount()
            .clickStartFresh()
            .setCurrency()

            .clickItemSuggestion("Cash")
            .clickAccountsNext()

            .clickItemSuggestion("Food & Drinks")
            .clickCategoriesFinish()
            .assertBalance(
                amount = "0",
                amountDecimal = ".00"
            )
    }


    private fun clickItemSuggestion(suggestion: String): OnboardingFlow {
        composeTestRule.onNodeWithText(suggestion)
            .performClick()
        return this
    }

    private fun clickAccountsNext(): OnboardingFlow {
        composeTestRule.onNodeWithText("Next")
            .performClick()
        return this
    }

    private fun clickCategoriesFinish(): HomeTab {
        composeTestRule.onNodeWithText("Finish")
            .performClick()
        return HomeTab(composeTestRule)
    }
}