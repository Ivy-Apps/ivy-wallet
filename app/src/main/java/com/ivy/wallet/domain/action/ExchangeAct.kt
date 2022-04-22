package com.ivy.wallet.domain.action

import arrow.core.Option
import com.ivy.fp.action.FPAction
import com.ivy.fp.then
import com.ivy.wallet.domain.pure.ExchangeData
import com.ivy.wallet.domain.pure.exchange
import com.ivy.wallet.io.persistence.dao.ExchangeRateDao
import java.math.BigDecimal
import javax.inject.Inject

class ExchangeAct @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
) : FPAction<ExchangeAct.Input, Option<BigDecimal>>() {
    override suspend fun Input.compose(): suspend () -> Option<BigDecimal> = suspend {
        io {
            exchange(
                data = data,
                amount = amount,
                getExchangeRate = exchangeRateDao::findByBaseCurrencyAndCurrency then {
                    it?.toDomain()
                }
            )
        }
    }

    data class Input(
        val data: ExchangeData,
        val amount: BigDecimal
    )
}

fun exchangeActInput(
    data: ExchangeData,
    amount: BigDecimal
): ExchangeAct.Input = ExchangeAct.Input(
    data = data,
    amount = amount
)
