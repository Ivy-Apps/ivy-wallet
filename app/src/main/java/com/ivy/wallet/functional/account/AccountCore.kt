package com.ivy.wallet.functional.account

import arrow.core.NonEmptyList
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

    return calculateAccountValues(
        accountId = accountId,
        accountTransactions = accountTransactions,
        valueFunctions = valueFunctions
    )
}

fun calculateAccountValues(
    accountId: UUID,
    accountTransactions: List<FPTransaction>,
    valueFunctions: NonEmptyList<(FPTransaction, accountId: UUID) -> BigDecimal>
): NonEmptyList<BigDecimal> {
    var sum = NonEmptyList.fromListUnsafe(
        List(valueFunctions.size) { BigDecimal.ZERO }
    )

    accountTransactions.map { transaction ->
        sum = NonEmptyList.fromListUnsafe(
            sum.mapIndexed { index, sumValue ->
                val valueFunction = valueFunctions[index]
                sumValue.plus(valueFunction(transaction, accountId))
            }
        )
    }

    return sum
}