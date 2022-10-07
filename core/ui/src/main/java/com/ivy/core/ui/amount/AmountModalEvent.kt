package com.ivy.core.ui.amount

import com.ivy.data.CurrencyCode
import com.ivy.data.Value

enum class CalculatorOption {
    Plus, Minus, Multiply, Divide, Brackets, Percent, Equals, C
}

sealed interface AmountModalEvent {
    data class Number(val number: Int) : AmountModalEvent
    data class Calculator(val option: CalculatorOption) : AmountModalEvent
    object DecimalSeparator : AmountModalEvent
    object Backspace : AmountModalEvent

    data class CurrencyChange(val currency: CurrencyCode) : AmountModalEvent
    data class Initial(val initialAmount: Value?) : AmountModalEvent
}