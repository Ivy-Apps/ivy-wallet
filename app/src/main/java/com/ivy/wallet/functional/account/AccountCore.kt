package com.ivy.wallet.functional.account

import arrow.core.NonEmptyList
import com.ivy.wallet.functional.core.calculateValueFunctionsSum
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.data.FPTransaction
import com.ivy.wallet.functional.data.toFPTransaction
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.util.*


suspend fun calculateAccountValues(
    transactionDao: TransactionDao,
    accountId: UUID,
    range: ClosedTimeRange,
    valueFunctions: NonEmptyList<(FPTransaction, accountId: UUID) -> BigDecimal>
): NonEmptyList<BigDecimal> {
    return calculateAccountValues(
        accountId = accountId,
        retrieveAccountTransactions = {
            transactionDao.findAllByAccountAndBetween(
                accountId = accountId,
                startDate = range.from,
                endDate = range.to
            )
        },
        retrieveToAccountTransfers = {
            transactionDao.findAllToAccountAndBetween(
                toAccountId = accountId,
                startDate = range.from,
                endDate = range.to
            )
        },
        valueFunctions = valueFunctions
    )
}

suspend fun calculateAccountValues(
    accountId: UUID,
    retrieveAccountTransactions: suspend (UUID) -> List<Transaction>,
    retrieveToAccountTransfers: suspend (UUID) -> List<Transaction>,
    valueFunctions: NonEmptyList<(FPTransaction, accountId: UUID) -> BigDecimal>
): NonEmptyList<BigDecimal> {
    val accountTrns = retrieveAccountTransactions(accountId)
        .plus(retrieveToAccountTransfers(accountId))
        .map { it.toFPTransaction() }

    return calculateValueFunctionsSum(
        valueFunctionArgument = accountId,
        transactions = accountTrns,
        valueFunctions = valueFunctions
    )
}