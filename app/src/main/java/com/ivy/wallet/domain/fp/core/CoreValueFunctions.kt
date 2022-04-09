package com.ivy.wallet.domain.fp.core

import arrow.core.toOption
import com.ivy.wallet.domain.fp.data.FPTransaction
import com.ivy.wallet.domain.fp.exchange
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.ExchangeRateDao
import java.math.BigDecimal

data class ExchangeData(
    val exchangeRateDao: ExchangeRateDao,
    val accountDao: AccountDao,
    val baseCurrencyCode: String,
    val toCurrency: String,
)

suspend fun amountInCurrency(fpTransaction: FPTransaction, data: ExchangeData): BigDecimal {
    val fromCurrencyCode =
        data.accountDao.findById(fpTransaction.accountId)?.currency.toOption()

    return exchange(
        exchangeRateDao = data.exchangeRateDao,
        baseCurrencyCode = data.baseCurrencyCode,
        fromCurrencyCode = fromCurrencyCode,
        fromAmount = fpTransaction.amount,
        toCurrencyCode = data.toCurrency
    ).orNull() ?: BigDecimal.ZERO
}