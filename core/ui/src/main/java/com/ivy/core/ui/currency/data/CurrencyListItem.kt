package com.ivy.core.ui.currency.data

import androidx.compose.runtime.Immutable

@Immutable
sealed interface CurrencyListItem {
    @Immutable
    data class Currency(val currency: CurrencyUi) : CurrencyListItem

    @Immutable
    data class SectionDivider(val name: String) : CurrencyListItem
}