package com.ivy.wallet.compose.helpers

import android.icu.util.Currency
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule

class CurrencyPicker<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun searchAndSelect(currency: Currency) {
        composeTestRule.onNodeWithTag("search_input")
            .performTextInput(currency.currencyCode)

        composeTestRule.onNodeWithText(currency.displayName)
            .performClick()

        composeTestRule.onNodeWithTag("set_currency_save")
            .performClick()
    }
}