package com.ivy.wallet.domain.fp.account

import arrow.core.NonEmptyList
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.fp.core.Pure
import com.ivy.wallet.domain.fp.core.Total
import com.ivy.wallet.domain.fp.core.calculateValueFunctionsSum
import com.ivy.wallet.domain.fp.data.ClosedTimeRange
import com.ivy.wallet.domain.fp.data.toFPTransaction
import com.ivy.wallet.io.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.util.*

@Total
suspend fun calcAccValues(
    transactionDao: TransactionDao,
    accountId: UUID,
    range: ClosedTimeRange,
    valueFunctions: NonEmptyList<AccountValueFunction>
): NonEmptyList<BigDecimal> {
    return calcAccValues(
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

@Total
suspend fun calcAccValues(
    accountId: UUID,
    retrieveAccountTransactions: suspend (UUID) -> List<Transaction>,
    retrieveToAccountTransfers: suspend (UUID) -> List<Transaction>,
    valueFunctions: NonEmptyList<AccountValueFunction>
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

@Pure
fun calcAccValues(
    accountId: UUID,
    accountsTrns: List<Transaction>,
    valueFunctions: NonEmptyList<AccountValueFunction>
): NonEmptyList<BigDecimal> {
    val accountTrns = accountsTrns
        .map { it.toFPTransaction() }

    return calculateValueFunctionsSum(
        valueFunctionArgument = accountId,
        transactions = accountTrns,
        valueFunctions = valueFunctions
    )
}