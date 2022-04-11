package com.ivy.wallet.domain.fp.wallet

import arrow.core.NonEmptyList
import arrow.core.Some
import com.ivy.wallet.domain.fp.account.AccountValueFunction
import com.ivy.wallet.domain.fp.account.calculateAccountValues
import com.ivy.wallet.domain.fp.core.Uncertain
import com.ivy.wallet.domain.fp.core.mapIndexedNel
import com.ivy.wallet.domain.fp.core.nonEmptyListOfZeros
import com.ivy.wallet.domain.fp.data.*
import com.ivy.wallet.domain.fp.exchangeToBaseCurrency
import com.ivy.wallet.io.persistence.dao.ExchangeRateDao
import com.ivy.wallet.utils.scopedIOThread
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.math.BigDecimal
import java.util.*

typealias UncertainWalletValues = Uncertain<List<CurrencyConvError>, NonEmptyList<BigDecimal>>
typealias AccountValuesPair = Pair<FPAccount, NonEmptyList<BigDecimal>>

suspend fun calculateWalletValues(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
    valueFunctions: NonEmptyList<AccountValueFunction>
): UncertainWalletValues {
    val uncertainWalletValues = walletDAOs.accountDao.findAll()
        .filter { !filterExcluded || it.includeInBalance }
        .map { account ->
            Pair(
                first = account.toFPAccount(baseCurrencyCode),
                second = calculateAccountValues(
                    transactionDao = walletDAOs.transactionDao,
                    accountId = account.id,
                    range = range,
                    valueFunctions = valueFunctions
                )
            )
        }
        .convertValuesInBaseCurrency(
            exchangeRateDao = walletDAOs.exchangeRateDao,
            baseCurrencyCode = baseCurrencyCode
        )

    return sumUncertainWalletValues(
        valueN = valueFunctions.size,
        uncertainWalletValues = uncertainWalletValues
    )
}

suspend fun calculateWalletValuesWithAccountFilters(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    accountIdFilterList: List<UUID>,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
    valueFunctions: NonEmptyList<AccountValueFunction>
): UncertainWalletValues {

    val accounts = scopedIOThread { scope ->
        if (accountIdFilterList.isNotEmpty())
            accountIdFilterList.map { accId ->
                scope.async {
                    walletDAOs.accountDao.findById(accId)
                }
            }.awaitAll().filterNotNull()
        else {
            walletDAOs.accountDao.findAll()
        }
    }

    val uncertainWalletValues = accounts
        .filter { !filterExcluded || it.includeInBalance }
        .map { account ->
            Pair(
                first = account.toFPAccount(baseCurrencyCode),
                second = calculateAccountValues(
                    transactionDao = walletDAOs.transactionDao,
                    accountId = account.id,
                    range = range,
                    valueFunctions = valueFunctions
                )
            )
        }
        .convertValuesInBaseCurrency(
            exchangeRateDao = walletDAOs.exchangeRateDao,
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
                baseCurrencyCode = baseCurrencyCode,
                fromCurrencyCode = account.currencyCode,
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