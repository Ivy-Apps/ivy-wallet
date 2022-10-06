package com.ivy.core.ui.currency.data

import androidx.compose.runtime.Immutable
import com.ivy.data.CurrencyCode

@Immutable
data class CurrencyUi(
    val code: CurrencyCode,
    val name: String,
)