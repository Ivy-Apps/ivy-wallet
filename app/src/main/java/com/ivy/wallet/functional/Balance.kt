package com.ivy.wallet.functional

import arrow.core.*
import arrow.typeclasses.Semigroup
import com.ivy.wallet.functional.account.calculateAccountBalance
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao
import timber.log.Timber
import java.math.BigDecimal

suspend fun calculateBalance(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String?,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy()
): Validated<List<CurrencyConversionError>, BigDecimal> {
    return accountDao.findAll()
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
}

private suspend fun List<Pair<Account, BigDecimal>>.sumInBaseCurrency(
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String?,
): Validated<List<CurrencyConversionError>, BigDecimal> {
    return traverseValidated(Semigroup.nonEmptyList()) { (account, accountBalance) ->
        Timber.i("Account ${account.name} has $accountBalance")
        exchangeToBaseCurrency(
            exchangeRateDao = exchangeRateDao,
            baseCurrencyCode = baseCurrencyCode.toOption(),
            fromCurrencyCode = account.currency.toOption(),
            fromAmount = accountBalance
        ).orNull()?.validNel() ?: CurrencyConversionError(account = account).invalidNel()
    }.map { balancesInBaseCurrency ->
        balancesInBaseCurrency.sumOf { it }
    }
}

data class CurrencyConversionError(val account: Account)
