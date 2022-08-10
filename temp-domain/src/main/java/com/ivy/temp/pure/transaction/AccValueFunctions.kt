package com.ivy.wallet.domain.pure.transaction

import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TransactionType
import java.math.BigDecimal
import java.util.*

typealias AccountValueFunction = ValueFunction<UUID>

object AccountValueFunctions {
    fun balance(
        transaction: TransactionOld,
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
        transaction: TransactionOld,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.INCOME)
            amount else BigDecimal.ZERO
    }

    fun transferIncome(
        transaction: TransactionOld,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.toAccountId == accountId && type == TransactionType.TRANSFER)
            toAmount else BigDecimal.ZERO
    }

    fun expense(
        transaction: TransactionOld,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.EXPENSE)
            amount else BigDecimal.ZERO
    }

    fun transferExpense(
        transaction: TransactionOld,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.TRANSFER)
            amount else BigDecimal.ZERO
    }


    fun incomeCount(
        transaction: TransactionOld,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.INCOME)
            BigDecimal.ONE else BigDecimal.ZERO
    }

    fun expenseCount(
        transaction: TransactionOld,
        accountId: UUID
    ): BigDecimal = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.EXPENSE)
            BigDecimal.ONE else BigDecimal.ZERO
    }
}