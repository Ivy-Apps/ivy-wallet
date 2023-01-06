package com.ivy.core.ui.amount

import com.ivy.data.CurrencyCode
import com.ivy.data.Value

sealed interface AmountModalEvent {
    data class Number(val number: Int) : AmountModalEvent
    data class CalculatorOperator(val operator: com.ivy.math.calculator.CalculatorOperator) :
        AmountModalEvent
    object DecimalSeparator : AmountModalEvent
    object Backspace : AmountModalEvent
    object CalculatorEquals : AmountModalEvent
    object CalculatorC : AmountModalEvent

    data class CurrencyChange(val currency: CurrencyCode) : AmountModalEvent
    data class Initial(val initialAmount: Value?) : AmountModalEvent
}