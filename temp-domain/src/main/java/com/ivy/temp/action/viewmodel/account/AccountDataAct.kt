package com.ivy.wallet.domain.action.viewmodel.account

import arrow.core.toOption
import com.ivy.base.AccountData
import com.ivy.data.AccountOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.temp.persistence.ExchangeActOld
import com.ivy.temp.persistence.ExchangeData
import com.ivy.wallet.domain.action.account.CalcAccBalanceAct
import com.ivy.wallet.domain.action.account.CalcAccIncomeExpenseAct
import javax.inject.Inject

class AccountDataAct @Inject constructor(
    private val exchangeAct: ExchangeActOld,
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
                ExchangeActOld.Input(
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
        val accounts: List<AccountOld>,
        val baseCurrency: String,
        val range: com.ivy.base.ClosedTimeRange,
        val includeTransfersInCalc: Boolean = false
    )
}