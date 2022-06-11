package com.ivy.wallet.compose.scenario

import android.icu.util.Currency
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.CurrencyPicker
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class OnboardingTest : IvyComposeTest() {

    private val currencyPicker = CurrencyPicker(composeTestRule)

    @Test
    fun OnboardingShortestPath() = testWithRetry {
        testWithRetry {
            onboarding.chooseOfflineAccount()
            onboarding.clickStartFresh()
            onboarding.setCurrency()
            onboarding.skipAccounts()
            onboarding.skipCategories()

            mainBottomBar.clickAccounts()

            composeTestRule.onNode(hasText("Cash"))
                .assertIsDisplayed()
        }
    }

    @Test
    fun OnboardingOfflineAccount_andSelectCurrency() = testWithRetry {
        onboarding.chooseOfflineAccount()
        onboarding.clickStartFresh()

        currencyPicker.searchAndSelect(Currency.getInstance("BGN"))
        onboarding.setCurrency()

        onboarding.skipAccounts()
        onboarding.skipCategories()

        mainBottomBar.clickAccounts()

        accountsTab.assertAccountBalance(
            account = "Cash",
            balance = "0",
            balanceDecimal = ".00",
            currency = "BGN",
        )
    }

    @Test
    fun Onboard_with1AccountAnd1Category() = testWithRetry {
        onboarding.onboardWith1AccountAnd1Category()
    }
}