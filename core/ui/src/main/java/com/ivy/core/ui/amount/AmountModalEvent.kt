package com.ivy.core.ui.amount

import com.ivy.data.CurrencyCode
import com.ivy.data.Value
import com.ivy.math.calculator.CalculatorOperator

sealed interface AmountModalEvent {
    data class Number(val number: Int) : AmountModalEvent
    data class Calculator(val option: CalculatorOperator) : AmountModalEvent
    object DecimalSeparator : AmountModalEvent
    object Backspace : AmountModalEvent
    object CalculatorEquals : AmountModalEvent
    object CalculatorC : AmountModalEvent

    data class CurrencyChange(val currency: CurrencyCode) : AmountModalEvent
    data class Initial(val initialAmount: Value?) : AmountModalEvent
}