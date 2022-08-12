package com.ivy.core.action.currency

import arrow.core.Option
import com.ivy.exchange.ExchangeRateDao
import com.ivy.exchange.exchange
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import javax.inject.Inject

class ExchangeAct @Inject constructor(
    private val baseCurrencyAct: BaseCurrencyAct,
    private val exchangeRateDao: ExchangeRateDao,
) : FPAction<ExchangeAct.Input, Option<Double>>() {
    data class Input(
        val fromCurrency: String,
        val toCurrency: String,
        val amount: Double
    )

    override suspend fun Input.compose(): suspend () -> Option<Double> = {
        exchange(
            baseCurrency = baseCurrencyAct(Unit),
            fromCurrency = fromCurrency,
            toCurrency = toCurrency,
            amount = amount,
            getExchangeRate = exchangeRateDao::findByBaseCurrencyAndCurrency then {
                it?.toDomain()
            }
        )
    }
}