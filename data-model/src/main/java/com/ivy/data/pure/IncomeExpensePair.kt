package com.ivy.data.pure

import java.math.BigDecimal

data class IncomeExpensePair(
    val income: BigDecimal,
    val expense: BigDecimal
) {
    companion object {
        fun zero(): IncomeExpensePair = IncomeExpensePair(BigDecimal.ZERO, BigDecimal.ZERO)
    }
}