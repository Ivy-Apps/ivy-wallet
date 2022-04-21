package com.ivy.wallet.domain.action.wallet

import arrow.core.Option
import com.ivy.wallet.domain.action.FPAction
import com.ivy.wallet.domain.fp.exchange
import com.ivy.wallet.io.persistence.dao.ExchangeRateDao
import java.math.BigDecimal
import javax.inject.Inject

class ExchangeAct @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
) : FPAction<ExchangeAct.Input, Option<BigDecimal>>() {

    override suspend fun Input.recipe(): suspend () -> Option<BigDecimal> = suspend {
        io {
            exchange(
                baseCurrencyCode = baseCurrency,
                exchangeRateDao = exchangeRateDao,
                fromAmount = amount,
                fromCurrencyCode = fromCurrency,
                toCurrencyCode = toCurrency
            )
        }
    }


    data class Input(
        val baseCurrency: String,
        val fromCurrency: Option<String>,
        val toCurrency: String,
        val amount: BigDecimal
    )
}
