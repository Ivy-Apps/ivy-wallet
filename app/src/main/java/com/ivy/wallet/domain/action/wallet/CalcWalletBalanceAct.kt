package com.ivy.wallet.domain.action.wallet

import com.ivy.wallet.domain.action.FPAction
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.then
import java.math.BigDecimal
import javax.inject.Inject

class CalcWalletBalanceAct @Inject constructor(
    private val accountsAct: AccountsAct,
    private val calcAccBalanceAct: CalcAccBalanceAct,
    private val exchangeAct: ExchangeAct,
    private val baseCurrencyAct: BaseCurrencyAct
) : FPAction<CalcWalletBalanceAct.Input, BigDecimal>() {

    override suspend fun Input.recipe(): suspend () -> BigDecimal = (accountsAct then {
        it.filter { acc -> acc.includeInBalance }
    } then {
        it.map { acc -> calcAccBalanceAct(CalcAccBalanceAct.Input(acc)) }
    } then baseCurrencyAct then {
        it.map { balanceOutput ->
            exchangeAct(
                ExchangeAct.Input(
                    baseCurrency =,
                    fromCurrency = balanceOutput.account.currency.toOption(),
                    toCurrency =,
                    amount = balanceOutput.balance
                )
            )
        }
    } then { balances ->
        balances.sumOf { it.orNull() ?: BigDecimal.ZERO }
    }).fixUnit()

    data class Input(
        val currency: String
    )
}
