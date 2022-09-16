package com.ivy.temp.persistence

import arrow.core.Option
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import java.math.BigDecimal
import javax.inject.Inject

@Deprecated("use `ExchangeAct`")
class ExchangeActOld @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
) : FPAction<ExchangeActOld.Input, Option<BigDecimal>>() {
    override suspend fun Input.compose(): suspend () -> Option<BigDecimal> = suspend {
        exchangeOld(
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
): ExchangeActOld.Input = ExchangeActOld.Input(
    data = data,
    amount = amount
)
