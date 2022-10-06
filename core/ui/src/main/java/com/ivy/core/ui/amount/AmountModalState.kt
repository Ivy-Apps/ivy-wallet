package com.ivy.core.ui.amount

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.data.CurrencyCode
import com.ivy.data.Value

@Immutable
internal class AmountModalState(
    val enteredText: String?,
    val currency: CurrencyCode,
    val amount: Value?,
    val amountBaseCurrency: ValueUi?,
)