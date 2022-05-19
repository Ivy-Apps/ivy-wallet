package com.ivy.wallet.domain.action.wallet

import arrow.core.toOption
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenFilter
import com.ivy.frp.action.thenMap
import com.ivy.frp.action.thenSum
import com.ivy.frp.fixUnit
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.account.CalcAccBalanceAct
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import java.math.BigDecimal
import javax.inject.Inject

class CalcWalletBalanceAct @Inject constructor(
    private val accountsAct: AccountsAct,
    private val calcAccBalanceAct: CalcAccBalanceAct,
    private val exchangeAct: ExchangeAct,
) : FPAction<CalcWalletBalanceAct.Input, BigDecimal>() {

    override suspend fun Input.compose(): suspend () -> BigDecimal = recipe().fixUnit()

    private suspend fun Input.recipe(): suspend (Unit) -> BigDecimal =
        accountsAct thenFilter {
            withExcluded || it.includeInBalance
        } thenMap {
            calcAccBalanceAct(
                CalcAccBalanceAct.Input(
                    account = it,
                    range = range
                )
            )
        } thenMap {
            exchangeAct(
                ExchangeAct.Input(
                    data = ExchangeData(
                        baseCurrency = baseCurrency,
                        fromCurrency = (it.account.currency ?: baseCurrency).toOption(),
                        toCurrency = balanceCurrency
                    ),
                    amount = it.balance
                )
            )
        } thenSum {
            it.orNull() ?: BigDecimal.ZERO
        }

    data class Input(
        val baseCurrency: String,
        val balanceCurrency: String = baseCurrency,
        val range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
        val withExcluded: Boolean = false
    )
}
