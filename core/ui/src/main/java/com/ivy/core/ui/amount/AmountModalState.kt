package com.ivy.core.ui.amount

import androidx.compose.runtime.Immutable
import com.ivy.data.CurrencyCode
import com.ivy.data.Value

@Immutable
internal class AmountModalState(
    val amountText: String,
    val currency: CurrencyCode,
    val amount: Value?,
    val amountBaseCurrency: Value?,
)