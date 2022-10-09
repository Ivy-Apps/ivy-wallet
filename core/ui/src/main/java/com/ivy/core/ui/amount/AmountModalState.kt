package com.ivy.core.ui.amount

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.amount.data.CalculatorResultUi
import com.ivy.data.CurrencyCode
import com.ivy.data.Value

@Immutable
internal data class AmountModalState(
    val expression: String?,
    val currency: CurrencyCode,
    val amount: Value?,
    val amountBaseCurrency: ValueUi?,
    val calculatorResult: CalculatorResultUi?,
)