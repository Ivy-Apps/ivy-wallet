package com.ivy.core.ui.currency

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.currency.data.CurrencyListItem

@Immutable
internal data class CurrencyModalState(
    val items: List<CurrencyListItem>
)