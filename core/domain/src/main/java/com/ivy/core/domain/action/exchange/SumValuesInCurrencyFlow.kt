package com.ivy.core.domain.action.exchange

import arrow.core.getOrElse
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.exchange.SumValuesInCurrencyFlow.Input
import com.ivy.core.domain.pure.exchange.exchange
import com.ivy.data.CurrencyCode
import com.ivy.data.Value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SumValuesInCurrencyFlow @Inject constructor(
    private val exchangeRatesFlow: ExchangeRatesFlow
) : FlowAction<Input, Value>() {
    /**
     * @param outputCurrency null for base currency
     */
    data class Input(
        val values: List<Value>,
        val outputCurrency: CurrencyCode? = null,
    )

    override fun createFlow(input: Input): Flow<Value> =
        exchangeRatesFlow().map { rates ->
            val outputCurrency = input.outputCurrency ?: rates.baseCurrency
            val sum = input.values.sumOf {
                exchange(
                    exchangeData = rates,
                    from = it.currency, to = outputCurrency,
                    amount = it.amount
                ).getOrElse { 0.0 }
            }
            Value(
                amount = sum,
                currency = outputCurrency
            )
        }.flowOn(Dispatchers.Default)
}