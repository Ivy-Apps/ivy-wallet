package com.ivy.wallet.compose.scenario

import android.icu.util.Currency
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.AccountsTab
import com.ivy.wallet.compose.helpers.CurrencyPicker
import com.ivy.wallet.compose.helpers.MainBottomBar
import com.ivy.wallet.compose.helpers.OnboardingFlow
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class OnboardingTest : IvyComposeTest() {

    private val onboarding = OnboardingFlow(composeTestRule)
    private val currencyPicker = CurrencyPicker(composeTestRule)
    private val mainBottomBar = MainBottomBar(composeTestRule)
    private val accountsTab = AccountsTab(composeTestRule)

    @Test
    fun contextLoads() {
    }

    @Test
    fun OnboardingShortestPath() {
        onboarding.chooseOfflineAccount()
        onboarding.clickStartFresh()
        onboarding.setCurrency()
        onboarding.skipAccounts()
        onboarding.skipCategories()

        mainBottomBar.clickAccounts()

        composeTestRule.onNode(hasText("Cash"))
            .assertIsDisplayed()
    }

    @Test
    fun OnboardingOfflineAccount_andSelectCurrency() {
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
    fun Onboard_with1AccountAnd1Category() {
        onboarding.onboardWith1AccountAnd1Category()
    }
}