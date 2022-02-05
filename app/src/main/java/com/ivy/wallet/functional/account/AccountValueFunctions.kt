package com.ivy.wallet.functional.account

import com.ivy.wallet.functional.data.FPTransaction
import com.ivy.wallet.model.TransactionType
import java.math.BigDecimal
import java.util.*

fun balanceValueFunction(
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

fun incomeValueFunction(
    fpTransaction: FPTransaction,
    accountId: UUID
): BigDecimal = with(fpTransaction) {
    if (this.accountId == accountId && type == TransactionType.INCOME)
        amount else BigDecimal.ZERO
}

fun expenseValueFunction(
    fpTransaction: FPTransaction,
    accountId: UUID
): BigDecimal = with(fpTransaction) {
    if (this.accountId == accountId && type == TransactionType.EXPENSE)
        amount else BigDecimal.ZERO
}

fun incomeCountValueFunction(
    fpTransaction: FPTransaction,
    accountId: UUID
): BigDecimal = with(fpTransaction) {
    if (this.accountId == accountId && type == TransactionType.INCOME)
        BigDecimal.ONE else BigDecimal.ZERO
}

fun expenseCountValueFunction(
    fpTransaction: FPTransaction,
    accountId: UUID
): BigDecimal = with(fpTransaction) {
    if (this.accountId == accountId && type == TransactionType.EXPENSE)
        BigDecimal.ONE else BigDecimal.ZERO
}