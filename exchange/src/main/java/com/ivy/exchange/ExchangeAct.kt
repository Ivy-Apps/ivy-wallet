package com.ivy.exchange

import arrow.core.Option
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import java.math.BigDecimal
import javax.inject.Inject

class ExchangeAct @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
) : FPAction<ExchangeAct.Input, Option<BigDecimal>>() {
    override suspend fun Input.compose(): suspend () -> Option<BigDecimal> = suspend {
        exchange(
            data = data,
            amount = amount,
            getExchangeRate = exchangeRateDao::findByBaseCurrencyAndCurrency then {
                it?.toDomain()
            }
        )
    }

    data class Input(
        val data: ExchangeData,
        val amount: BigDecimal
    )
}

fun actInput(
    data: ExchangeData,
    amount: BigDecimal
): ExchangeAct.Input = ExchangeAct.Input(
    data = data,
    amount = amount
)
