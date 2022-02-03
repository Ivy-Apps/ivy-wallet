package com.ivy.wallet.functional

import arrow.core.toOption
import com.ivy.wallet.functional.account.calculateAccountBalance
import com.ivy.wallet.functional.core.Uncertain
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal

suspend fun calculateBalance(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String?,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy()
): Uncertain<List<CurrencyConversionError>, BigDecimal> {
    val result = accountDao.findAll()
        .filter { !filterExcluded || it.includeInBalance }
        .map {
            Pair(
                first = it,
                second = calculateAccountBalance(
                    transactionDao = transactionDao,
                    accountId = it.id,
                    range = range
                )
            )
        }.sumInBaseCurrency(
            exchangeRateDao = exchangeRateDao,
            baseCurrencyCode = baseCurrencyCode,
        )

    return result
}

private suspend fun List<Pair<Account, BigDecimal>>.sumInBaseCurrency(
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String?,
): Uncertain<List<CurrencyConversionError>, BigDecimal> {
    var result = Uncertain(emptyList<CurrencyConversionError>(), BigDecimal.ZERO)

    this.forEach { (account, accountBalance) ->
        val balanceInBaseCurrency = exchangeToBaseCurrency(
            exchangeRateDao = exchangeRateDao,
            baseCurrencyCode = baseCurrencyCode.toOption(),
            fromCurrencyCode = account.currency.toOption(),
            fromAmount = accountBalance
        ).orNull()

        result = Uncertain(
            error = if (balanceInBaseCurrency == null) {
                //append error if balance can't be converted
                result.error.plus(CurrencyConversionError(account = account))
            } else result.error,
            value = balanceInBaseCurrency?.let {
                //sum balance only if it can be converted
                result.value + it
            } ?: result.value
        )
    }

    return result
}

data class CurrencyConversionError(val account: Account)
