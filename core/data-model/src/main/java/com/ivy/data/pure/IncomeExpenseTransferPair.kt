package com.ivy.data.pure

import java.math.BigDecimal

@Deprecated("don't use")
data class IncomeExpenseTransferPair(
    val income: BigDecimal,
    val expense: BigDecimal,
    val transferIncome: BigDecimal,
    val transferExpense: BigDecimal
) {
    companion object {
        fun zero(): IncomeExpenseTransferPair = IncomeExpenseTransferPair(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )
    }
}
