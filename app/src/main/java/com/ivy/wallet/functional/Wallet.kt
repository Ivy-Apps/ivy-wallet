package com.ivy.wallet.functional

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import arrow.core.toOption
import com.ivy.wallet.functional.account.balanceValueFunction
import com.ivy.wallet.functional.account.calculateAccountValues
import com.ivy.wallet.functional.core.Uncertain
import com.ivy.wallet.functional.core.mapIndexedNel
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.data.CurrencyConvError
import com.ivy.wallet.functional.data.FPTransaction
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.util.*

suspend fun calculateWalletBalance(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
): Uncertain<List<CurrencyConvError>, BigDecimal> {
    val uncertainValues = calculateWalletValues(
        accountDao = accountDao,
        transactionDao = transactionDao,
        exchangeRateDao = exchangeRateDao,
        baseCurrencyCode = baseCurrencyCode,
        filterExcluded = filterExcluded,
        range = range,
        valueFunctions = nonEmptyListOf(
            ::balanceValueFunction
        )
    )

    return Uncertain(
        error = uncertainValues.error,
        value = uncertainValues.value.head
    )
}

suspend fun calculateWalletValues(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
    valueFunctions: NonEmptyList<(FPTransaction, accountId: UUID) -> BigDecimal>
): Uncertain<List<CurrencyConvError>, NonEmptyList<BigDecimal>> {
    return accountDao.findAll()
        .filter { !filterExcluded || it.includeInBalance }
        .map {
            Pair(
                first = it,
                second = calculateAccountValues(
                    transactionDao = transactionDao,
                    accountId = it.id,
                    range = range,
                    valueFunctions = valueFunctions
                )
            )
        }
        .map { (account, accountValues) ->
            val valuesInBaseCurrency = accountValues.map {
                exchangeToBaseCurrency(
                    exchangeRateDao = exchangeRateDao,
                    baseCurrencyCode = baseCurrencyCode.toOption(),
                    fromCurrencyCode = account.currency.toOption(),
                    fromAmount = it
                )
            }
            val hasError = valuesInBaseCurrency.any { !it.isDefined() }

            Uncertain(
                error = if (hasError)
                    listOf(CurrencyConvError(account = account)) else emptyList(),
                value = if (!hasError) {
                    //if there is no error all values must be Some()
                    valuesInBaseCurrency.map { it.orNull()!! }
                } else NonEmptyList.fromListUnsafe(
                    List(accountValues.size) { BigDecimal.ZERO }
                )
            )
        }.sum(
            valuesN = valueFunctions.size
        )
}

private fun Iterable<Uncertain<List<CurrencyConvError>, NonEmptyList<BigDecimal>>>.sum(
    valuesN: Int
): Uncertain<List<CurrencyConvError>, NonEmptyList<BigDecimal>> {
    var sum = Uncertain(
        error = emptyList<CurrencyConvError>(),
        value = NonEmptyList.fromListUnsafe(
            List(valuesN) { BigDecimal.ZERO }
        )
    )

    this.forEach { accountUncertain ->
        sum = Uncertain(
            error = sum.error.plus(accountUncertain.error),
            value = if (accountUncertain.isCertain()) {
                sum.value.mapIndexedNel { index, value ->
                    value.plus(accountUncertain.value[index])
                }
            } else sum.value //no need to sum it, if it's uncertain (it'll be all ZEROs)

        )
    }

    return sum
}