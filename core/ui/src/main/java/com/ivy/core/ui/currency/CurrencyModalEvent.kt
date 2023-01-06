package com.ivy.core.ui.currency

import com.ivy.core.ui.currency.data.CurrencyUi
import com.ivy.data.CurrencyCode

internal sealed interface CurrencyModalEvent {
    data class Search(val query: String) : CurrencyModalEvent
    data class SelectCurrency(val currencyUi: CurrencyUi) : CurrencyModalEvent
    data class SelectCurrencyCode(val currencyCode: CurrencyCode) : CurrencyModalEvent
    data class Initial(val initialCurrency: CurrencyCode) : CurrencyModalEvent
}