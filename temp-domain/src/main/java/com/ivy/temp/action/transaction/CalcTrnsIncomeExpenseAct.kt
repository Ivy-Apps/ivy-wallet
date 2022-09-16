package com.ivy.wallet.domain.action.transaction

import arrow.core.nonEmptyListOf
import com.ivy.data.AccountOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.temp.persistence.ExchangeActOld
import com.ivy.temp.persistence.actInput
import com.ivy.wallet.domain.pure.data.IncomeExpenseTransferPair
import com.ivy.wallet.domain.pure.transaction.WalletValueFunctions
import com.ivy.wallet.domain.pure.transaction.foldTransactionsSuspend
import javax.inject.Inject

class CalcTrnsIncomeExpenseAct @Inject constructor(
    private val exchangeAct: ExchangeActOld
) : FPAction<CalcTrnsIncomeExpenseAct.Input, IncomeExpenseTransferPair>() {
    override suspend fun Input.compose(): suspend () -> IncomeExpenseTransferPair = suspend {
        foldTransactionsSuspend(
            transactions = transactions,
            valueFunctions = nonEmptyListOf(
                WalletValueFunctions::income,
                WalletValueFunctions::expense,
                WalletValueFunctions::transferIncome,
                WalletValueFunctions::transferExpenses
            ),
            arg = WalletValueFunctions.Argument(
                accounts = accounts,
                baseCurrency = baseCurrency,
                exchange = ::actInput then exchangeAct
            )
        )
    } then { values ->
        IncomeExpenseTransferPair(
            income = values[0],
            expense = values[1],
            transferIncome = values[2],
            transferExpense = values[3]
        )
    }

    data class Input(
        val transactions: List<TransactionOld>,
        val baseCurrency: String,
        val accounts: List<AccountOld>
    )
}