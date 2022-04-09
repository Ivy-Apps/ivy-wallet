package com.ivy.wallet.domain.fp.account

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.fp.core.ValueFunction
import com.ivy.wallet.domain.fp.data.FPTransaction
import java.math.BigDecimal
import java.util.*

typealias AccountValueFunction = ValueFunction<UUID>

object AccountValueFunctions {
    fun balance(
        fpTransaction: FPTransaction,
        accountId: UUID
    ): BigDecimal = with(fpTransaction) {
        if (this.accountId == accountId) {
            //Account's transactions
            when (type) {
                TransactionType.INCOME -> amount
                TransactionType.EXPENSE -> amount.negate()
                TransactionType.TRANSFER -> {
                    if (toAccountId.orNull() != accountId) {
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
            toAccountId.orNull()?.takeIf { it == accountId } ?: return BigDecimal.ZERO
            toAmount
        }
    }

    fun income(
        fpTransaction: FPTransaction,
        accountId: UUID
    ): BigDecimal = with(fpTransaction) {
        if (this.accountId == accountId && type == TransactionType.INCOME)
            amount else BigDecimal.ZERO
    }

    fun expense(
        fpTransaction: FPTransaction,
        accountId: UUID
    ): BigDecimal = with(fpTransaction) {
        if (this.accountId == accountId && type == TransactionType.EXPENSE)
            amount else BigDecimal.ZERO
    }

    fun incomeCount(
        fpTransaction: FPTransaction,
        accountId: UUID
    ): BigDecimal = with(fpTransaction) {
        if (this.accountId == accountId && type == TransactionType.INCOME)
            BigDecimal.ONE else BigDecimal.ZERO
    }

    fun expenseCount(
        fpTransaction: FPTransaction,
        accountId: UUID
    ): BigDecimal = with(fpTransaction) {
        if (this.accountId == accountId && type == TransactionType.EXPENSE)
            BigDecimal.ONE else BigDecimal.ZERO
    }
}