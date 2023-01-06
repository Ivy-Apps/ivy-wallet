package com.ivy.core.domain.action.exchange

import arrow.core.getOrElse
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.exchange.ExchangeFlow.Input
import com.ivy.core.domain.pure.exchange.exchange
import com.ivy.data.CurrencyCode
import com.ivy.data.Value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExchangeFlow @Inject constructor(
    private val exchangeRatesFlow: ExchangeRatesFlow
) : FlowAction<Input, Value>() {
    /**
     * @param outputCurrency null for baseCurrency
     */
    data class Input(
        val value: Value,
        val outputCurrency: CurrencyCode? = null,
    )

    override fun Input.createFlow(): Flow<Value> =
        exchangeRatesFlow().map { rates ->
            val outputCurrency = this.outputCurrency ?: rates.baseCurrency
            val exchangedAmount = exchange(
                exchangeData = rates,
                from = value.currency,
                to = outputCurrency,
                amount = value.amount
            ).getOrElse { 0.0 }
            Value(exchangedAmount, outputCurrency)
        }
}