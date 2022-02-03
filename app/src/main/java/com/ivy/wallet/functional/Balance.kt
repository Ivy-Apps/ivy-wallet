package com.ivy.wallet.functional

import arrow.core.*
import com.ivy.wallet.functional.account.calculateAccountBalance
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.util.*

suspend fun calculateBalance(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    filterExcluded: Boolean = true,
    baseCurrencyCode: String?,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy()
): ValidatedNel<CurrencyConversionError, BigDecimal> {
    return accountDao.findAll()
        .filter { !filterExcluded || it.includeInBalance }
        .map {
            Pair(
                it,
                calculateAccountBalance(
                    transactionDao = transactionDao,
                    accountId = it.id,
                    range = range
                )
            )
        }.sumOf { (account, accountBalance) ->
            exchangeToBaseCurrency(
                exchangeRateDao = exchangeRateDao,
                baseCurrencyCode = baseCurrencyCode.toOption(),
                fromCurrencyCode = account.currency.toOption(),
                fromAmount = accountBalance
            )
            //TODO: WIP
        }
}

data class CurrencyConversionError(val account: Account)
