package com.ivy.core.ui.amount

import com.ivy.data.CurrencyCode
import com.ivy.data.Value

sealed interface AmountModalEvent {
    data class AmountChange(val amount: String) : AmountModalEvent
    data class CurrencyChange(val currency: CurrencyCode) : AmountModalEvent
    data class Initial(val initialAmount: Value) : AmountModalEvent
}