package com.ivy.wallet.domain.action.wallet

import arrow.core.toOption
import com.ivy.wallet.domain.action.FPAction
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.fixUnit
import com.ivy.wallet.domain.action.then
import java.math.BigDecimal
import javax.inject.Inject

class CalcWalletBalanceAct @Inject constructor(
    private val accountsAct: AccountsAct,
    private val calcAccBalanceAct: CalcAccBalanceAct,
    private val exchangeAct: ExchangeAct,
) : FPAction<CalcWalletBalanceAct.Input, BigDecimal>() {

    override suspend fun Input.compose(): suspend () -> BigDecimal = recipe().fixUnit()

    private suspend fun Input.recipe(): suspend (Unit) -> BigDecimal =
        accountsAct then {
            it.filter { acc -> acc.includeInBalance }
        } then {
            it.map { acc -> calcAccBalanceAct(CalcAccBalanceAct.Input(acc)) }
        } then {
            it.map { balanceOutput ->
                exchangeAct(
                    ExchangeAct.Input(
                        baseCurrency = baseCurrency,
                        fromCurrency = balanceOutput.account.currency.toOption(),
                        toCurrency = balanceCurrency,
                        amount = balanceOutput.balance
                    )
                )
            }
        } then { balances ->
            balances.sumOf { it.orNull() ?: BigDecimal.ZERO }
        }

    data class Input(
        val baseCurrency: String,
        val balanceCurrency: String
    )
}
