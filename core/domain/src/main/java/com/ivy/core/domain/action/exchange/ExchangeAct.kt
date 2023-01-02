package com.ivy.core.domain.action.exchange

import arrow.core.getOrElse
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.pure.exchange.exchange
import com.ivy.data.Value
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExchangeAct @Inject constructor(
    private val exchangeRatesFlow: ExchangeRatesFlow,
) : Action<ExchangeAct.Input, Value>() {
    data class Input(
        val value: Value,
        val outputCurrency: String,
    )

    override suspend fun Input.willDo(): Value {
        val rates = exchangeRatesFlow().first()
        return Value(
            amount = exchange(
                exchangeData = rates,
                from = value.currency,
                to = outputCurrency,
                amount = value.amount
            ).getOrElse {
                value.amount // exchange as 1:1 if failed
            },
            currency = outputCurrency
        )
    }
}