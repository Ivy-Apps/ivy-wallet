package com.ivy.wallet.functional.wallet

import arrow.core.NonEmptyList
import arrow.core.Some
import arrow.core.toOption
import com.ivy.wallet.functional.account.calculateAccountValues
import com.ivy.wallet.functional.core.Uncertain
import com.ivy.wallet.functional.core.mapIndexedNel
import com.ivy.wallet.functional.core.nonEmptyListOfZeros
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.data.CurrencyConvError
import com.ivy.wallet.functional.data.FPTransaction
import com.ivy.wallet.functional.exchangeToBaseCurrency
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.util.*

typealias UncertainWalletValues = Uncertain<List<CurrencyConvError>, NonEmptyList<BigDecimal>>
typealias AccountValuesPair = Pair<Account, NonEmptyList<BigDecimal>>

suspend fun calculateWalletValues(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
    valueFunctions: NonEmptyList<(FPTransaction, accountId: UUID) -> BigDecimal>
): UncertainWalletValues {
    val uncertainWalletValues = accountDao.findAll()
        .filter { !filterExcluded || it.includeInBalance }
        .map { account ->
            Pair(
                first = account,
                second = calculateAccountValues(
                    transactionDao = transactionDao,
                    accountId = account.id,
                    range = range,
                    valueFunctions = valueFunctions
                )
            )
        }
        .convertValuesInBaseCurrency(
            exchangeRateDao = exchangeRateDao,
            baseCurrencyCode = baseCurrencyCode
        )

    return sumUncertainWalletValues(
        valueN = valueFunctions.size,
        uncertainWalletValues = uncertainWalletValues
    )
}

private suspend fun Iterable<AccountValuesPair>.convertValuesInBaseCurrency(
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
): List<UncertainWalletValues> {
    return this.map { (account, values) ->
        val valuesInBaseCurrency = values.map {
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
                valuesInBaseCurrency.map { (it as Some).value }
            } else nonEmptyListOfZeros(values.size)
        )
    }
}

private tailrec fun sumUncertainWalletValues(
    valueN: Int,
    uncertainWalletValues: List<UncertainWalletValues>,
    sum: UncertainWalletValues = Uncertain(
        error = emptyList(),
        value = nonEmptyListOfZeros(n = valueN)
    )
): UncertainWalletValues {
    return if (uncertainWalletValues.isEmpty()) sum else {
        val uncertainValues = uncertainWalletValues.first()

        sumUncertainWalletValues(
            valueN = valueN,
            uncertainWalletValues = uncertainWalletValues.drop(1),
            sum = Uncertain(
                error = sum.error.plus(uncertainValues.error),
                value = if (uncertainValues.isCertain()) {
                    sum.value.mapIndexedNel { index, value ->
                        value.plus(uncertainValues.value[index])
                    }
                } else sum.value //no need to sum it, if it's uncertain (it'll be all ZEROs)

            )
        )
    }
}