package com.ivy.wallet.functional.category

import com.ivy.wallet.functional.data.FPTransaction
import com.ivy.wallet.model.TransactionType
import java.math.BigDecimal
import java.util.*

object CategoryValueFunctions {
    data class Argument(
        val categoryId: UUID?,
        val trnCurrencyCode: String?
    )

    fun balance(
        fpTransaction: FPTransaction,
        categoryId: UUID?
    ): BigDecimal = with(fpTransaction) {
        if (this.categoryId.orNull() == categoryId) {
            when (type) {
                TransactionType.INCOME -> amount
                TransactionType.EXPENSE -> amount.negate()
                TransactionType.TRANSFER -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }
}