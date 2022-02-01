package com.ivy.wallet.functional

import arrow.core.nonEmptyListOf
import com.ivy.wallet.base.beginningOfIvyTime
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.functional.data.FPTransaction
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


suspend fun calculateAccountBalance(
    transactionDao: TransactionDao,
    accountId: UUID,
    fromDate: LocalDateTime = beginningOfIvyTime(),
    toDate: LocalDateTime = timeNowUTC(),
): BigDecimal {
    return calculateAccountValues(
        transactionDao = transactionDao,
        accountId = accountId,
        fromDate = fromDate,
        toDate = toDate,
        valueFunctions = nonEmptyListOf(
            ::balanceValueFunction
        )
    ).head
}


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
                    toAmount.orNull()?.minus(amount) ?: BigDecimal.ZERO
                }
            }
        }
    } else {
        //potential transfer to account?
        toAccountId.orNull()?.takeIf { it == accountId } ?: return BigDecimal.ZERO
        toAmount.orNull() ?: BigDecimal.ZERO
    }
}