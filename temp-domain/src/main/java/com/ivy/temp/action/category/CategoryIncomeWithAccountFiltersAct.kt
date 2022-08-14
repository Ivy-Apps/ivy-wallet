package com.ivy.wallet.domain.action.category

import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.wallet.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.ivy.wallet.domain.pure.data.IncomeExpenseTransferPair
import javax.inject.Inject

class CategoryIncomeWithAccountFiltersAct @Inject constructor(
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct
) : FPAction<CategoryIncomeWithAccountFiltersAct.Input, IncomeExpenseTransferPair>() {

    override suspend fun Input.compose(): suspend () -> IncomeExpenseTransferPair = suspend {
        val accountFilterSet = accountFilterList.map { it.id }.toHashSet()
        transactions.filter {
            it.categoryId == category?.id
        }.filter {
            if (accountFilterSet.isEmpty())
                true
            else
                accountFilterSet.contains(it.accountId)
        }
    } then {
        CalcTrnsIncomeExpenseAct.Input(
            transactions = it,
            baseCurrency = baseCurrency,
            accounts = accountFilterList
        )
    } then calcTrnsIncomeExpenseAct

    data class Input(
        val transactions: List<TransactionOld>,
        val accountFilterList: List<AccountOld>,
        val category: CategoryOld?,
        val baseCurrency: String
    )
}