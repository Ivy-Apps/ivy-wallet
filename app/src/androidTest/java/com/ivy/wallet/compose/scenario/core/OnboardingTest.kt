package com.ivy.wallet.compose.scenario.core

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import com.ivy.data.IvyCurrency
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.component.picker.IvyCurrencyPicker
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class OnboardingTest : IvyComposeTest() {
    private val currencyPicker = IvyCurrencyPicker(composeTestRule)

    @Test
    fun OnboardingShortestPath() = testWithRetry {
        chooseOfflineAccount()
            .clickStartFresh()
            .setCurrency()
            .skipAccounts()
            .skipCategories()
            .clickAccountsTab()

        composeTestRule.onNode(hasText("Cash"))
            .assertIsDisplayed()
    }

    @Test
    fun OnboardingOfflineAccount_andSelectCurrency() = testWithRetry {
        chooseOfflineAccount()
            .clickStartFresh()
        //TODO: Refactor this
        currencyPicker.searchAndSelect(IvyCurrency.fromCode("BGN") ?: error("Invalid currency"))
        setCurrency()
            .skipAccounts()
            .skipCategories()
            .clickAccountsTab()
            .assertAccountBalance(
                account = "Cash",
                balance = "0",
                balanceDecimal = ".00",
                currency = "BGN",
            )
    }

    @Test
    fun Onboard_with1AccountAnd1Category() = testWithRetry {
        onboardWith1AccountAnd1Category()
    }
}