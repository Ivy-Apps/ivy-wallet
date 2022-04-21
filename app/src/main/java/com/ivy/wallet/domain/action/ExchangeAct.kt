package com.ivy.wallet.domain.action

import arrow.core.Option
import com.ivy.wallet.domain.action.framework.FPAction
import com.ivy.wallet.domain.fp.exchange
import com.ivy.wallet.io.persistence.dao.ExchangeRateDao
import java.math.BigDecimal
import javax.inject.Inject

class ExchangeAct @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
) : FPAction<ExchangeAct.Input, Option<BigDecimal>>() {

    override suspend fun Input.compose(): suspend () -> Option<BigDecimal> = suspend {
        io {
            exchange(
                baseCurrencyCode = baseCurrency,
                amount = amount,
                fromCurrencyCode = fromCurrency,
                toCurrencyCode = toCurrency,
                getExchangeRate = exchangeRateDao::findByBaseCurrencyAndCurrency
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
