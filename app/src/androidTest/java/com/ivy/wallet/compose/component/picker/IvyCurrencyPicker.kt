package com.ivy.wallet.compose.component.picker

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.ivy.data.IvyCurrency
import com.ivy.wallet.compose.IvyComposeTestRule

class IvyCurrencyPicker(
    private val composeTestRule: IvyComposeTestRule
) {
    fun chooseCurrency(currencyCode: String) {
        searchAndSelect(
            IvyCurrency.fromCode(currencyCode) ?: error("unknown currency")
        )
        modalSave()
    }

    fun searchAndSelect(
        currency: IvyCurrency,
    ) {
        composeTestRule.onNodeWithTag("search_input")
            .performTextInput(currency.code)

        composeTestRule.onNodeWithText(currency.name)
            .performClick()
    }

    private fun modalSave() {
        composeTestRule.onNodeWithTag("set_currency_save")
            .performClick()
    }
}

interface CurrencyPicker<T> {
    fun chooseCurrency(currencyCode: String): T
}