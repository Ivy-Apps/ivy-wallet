package com.ivy.wallet.domain.pure

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.core.ValueFunction
import java.math.BigDecimal
import java.util.*

typealias AccountValueFunction = ValueFunction<UUID>

object AccountValueFunctions {
    fun balance(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.accountId == accountId) {
            //Account's transactions
            when (type) {
                TransactionType.INCOME -> amount
                TransactionType.EXPENSE -> amount.negate()
                TransactionType.TRANSFER -> {
                    if (toAccountId != accountId) {
                        //transfer to another account
                        amount.negate()
                    } else {
                        //transfer to self
                        toAmount.minus(amount)
                    }
                }
            }
        } else {
            //potential transfer to account?
            toAccountId?.takeIf { it == accountId } ?: return BigDecimal.ZERO
            toAmount
        }
    }

    fun income(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.INCOME)
            amount else BigDecimal.ZERO
    }

    fun expense(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.EXPENSE)
            amount else BigDecimal.ZERO
    }

    fun incomeCount(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.INCOME)
            BigDecimal.ONE else BigDecimal.ZERO
    }

    fun expenseCount(
        transaction: Transaction,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.EXPENSE)
            BigDecimal.ONE else BigDecimal.ZERO
    }
}