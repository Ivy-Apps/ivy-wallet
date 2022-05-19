package com.ivy.wallet.domain.action.viewmodel.account

import arrow.core.toOption
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.domain.action.account.CalcAccBalanceAct
import com.ivy.wallet.domain.action.account.CalcAccIncomeExpenseAct
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import com.ivy.wallet.ui.accounts.AccountData
import javax.inject.Inject

class AccountDataAct @Inject constructor(
    private val exchangeAct: ExchangeAct,
    private val calcAccBalanceAct: CalcAccBalanceAct,
    private val calcAccIncomeExpenseAct: CalcAccIncomeExpenseAct
) : FPAction<AccountDataAct.Input, List<AccountData>>() {

    override suspend fun Input.compose(): suspend () -> List<AccountData> = suspend {
        accounts
    } thenMap { acc ->
        val balance = calcAccBalanceAct(
            CalcAccBalanceAct.Input(
                account = acc
            )
        ).balance

        val balanceBaseCurrency = if (acc.currency != baseCurrency) {
            exchangeAct(
                ExchangeAct.Input(
                    data = ExchangeData(
                        baseCurrency = baseCurrency,
                        fromCurrency = acc.currency.toOption()
                    ),
                    amount = balance
                )
            ).orNull()
        } else {
            null
        }

        val incomeExpensePair = calcAccIncomeExpenseAct(
            CalcAccIncomeExpenseAct.Input(
                account = acc,
                range = range,
                includeTransfersInCalc = includeTransfersInCalc
            )
        ).incomeExpensePair

        AccountData(
            account = acc,
            balance = balance.toDouble(),
            balanceBaseCurrency = balanceBaseCurrency?.toDouble(),
            monthlyIncome = incomeExpensePair.income.toDouble(),
            monthlyExpenses = incomeExpensePair.expense.toDouble(),
        )
    }

    data class Input(
        val accounts: List<Account>,
        val baseCurrency: String,
        val range: ClosedTimeRange,
        val includeTransfersInCalc: Boolean = false
    )
}