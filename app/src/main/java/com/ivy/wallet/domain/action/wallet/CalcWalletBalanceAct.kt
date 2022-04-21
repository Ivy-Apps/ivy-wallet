package com.ivy.wallet.domain.action.wallet

import arrow.core.toOption
import com.ivy.wallet.domain.action.ExchangeAct
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.account.CalcAccBalanceAct
import com.ivy.wallet.domain.action.framework.*
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
            it.includeInBalance
        } thenMap {
            calcAccBalanceAct(CalcAccBalanceAct.Input(it))
        } thenMap {
            exchangeAct(
                ExchangeAct.Input(
                    baseCurrency = baseCurrency,
                    fromCurrency = it.account.currency.toOption(),
                    toCurrency = balanceCurrency,
                    amount = it.balance
                )
            )
        } thenSum {
            it.orNull() ?: BigDecimal.ZERO
        }

    data class Input(
        val baseCurrency: String,
        val balanceCurrency: String
    )
}
