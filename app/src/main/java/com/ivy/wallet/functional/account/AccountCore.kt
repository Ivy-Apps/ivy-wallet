package com.ivy.wallet.functional.account

import arrow.core.NonEmptyList
import com.ivy.wallet.functional.core.mapIndexedNel
import com.ivy.wallet.functional.core.nonEmptyListOfZeros
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
    val accountTransactions = retrieveAccountTransactions(accountId)
        .plus(retrieveToAccountTransfers(accountId))
        .map { it.toFPTransaction() }

    return sumAccountValues(
        accountId = accountId,
        accountTrns = accountTransactions,
        valueFunctions = valueFunctions
    )
}

tailrec fun sumAccountValues(
    accountId: UUID,
    accountTrns: List<FPTransaction>,
    valueFunctions: NonEmptyList<(FPTransaction, accountId: UUID) -> BigDecimal>,
    sum: NonEmptyList<BigDecimal> = nonEmptyListOfZeros(n = valueFunctions.size)
): NonEmptyList<BigDecimal> {
    return if (accountTrns.isEmpty())
        sum
    else
        sumAccountValues(
            accountId = accountId,
            accountTrns = accountTrns.drop(1),
            valueFunctions = valueFunctions,
            sum = sum.mapIndexedNel { index, sumValue ->
                val valueFunction = valueFunctions[index]
                sumValue + valueFunction(accountTrns.first(), accountId)
            }
        )
}