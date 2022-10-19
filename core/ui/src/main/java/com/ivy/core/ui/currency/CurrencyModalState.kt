package com.ivy.core.ui.currency

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.currency.data.CurrencyListItem
import com.ivy.core.ui.currency.data.CurrencyUi
import com.ivy.data.CurrencyCode

@Immutable
internal data class CurrencyModalState(
    val items: List<CurrencyListItem>,
    val suggested: List<CurrencyCode>,
    val selectedCurrency: CurrencyUi?,
    val searchQuery: String
)