package com.ivy.core.action.currency.exchange

import arrow.core.Option
import arrow.core.computations.option
import com.ivy.data.CurrencyCode
import com.ivy.frp.action.FPAction
import javax.inject.Inject

class ExchangeAct @Inject constructor(
    private val exchangeRateAct: ExchangeRateAct
) : FPAction<ExchangeAct.Input, Option<Double>>() {
    data class Input(
        val from: CurrencyCode,
        val to: CurrencyCode,
        val amount: Double
    )

    override suspend fun Input.compose(): suspend () -> Option<Double> = {
        exchange()
    }

    private suspend fun Input.exchange() = option {
        if (from == to) return@option amount
        if (amount == 0.0) return@option 0.0

        val rate = exchangeRateAct(
            ExchangeRateAct.Input(
                from = from,
                to = to,
            )
        ).bind()

        rate * amount
    }
}