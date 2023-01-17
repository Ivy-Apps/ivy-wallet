package com.ivy.core.ui.action

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.exchange.ExchangeFlow
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.data.Value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExchangeInBaseCurrencyFlow @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val exchangeFlow: ExchangeFlow,
) : FlowAction<Value, ValueUi?>() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createFlow(input: Value): Flow<ValueUi?> =
        baseCurrencyFlow().map { baseCurrency ->
            if (input.currency != baseCurrency) {
                exchangeFlow(ExchangeFlow.Input(input, baseCurrency))
            } else {
                flowOf(null)
            }
        }.flatMapLatest { flow ->
            flow.map { value ->
                value?.let {
                    format(it, shortenFiat = true)
                }
            }
        }
}