package com.ivy.wallet.domain.action.charts

import com.ivy.fp.action.FPAction
import com.ivy.fp.action.then
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.category.CategoryIncomeWithAccountFiltersAct
import com.ivy.wallet.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.ivy.wallet.domain.action.transaction.TrnsWithRangeAndAccFiltersAct
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.pure.account.filterExcluded
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.statistic.level1.CategoryAmount
import java.util.*
import javax.inject.Inject

class PieChartAct @Inject constructor(
    private val accountsAct: AccountsAct,
    private val trnsWithRangeAndAccFiltersAct: TrnsWithRangeAndAccFiltersAct,
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct,
    private val categoriesAct: CategoriesAct,
    private val categoryIncomeWithAccountFiltersAct: CategoryIncomeWithAccountFiltersAct
) : FPAction<PieChartAct.Input, PieChartAct.Output>() {
    override suspend fun Input.compose(): suspend () -> Output = suspend {
        val allAccounts = accountsAct(Unit)
        val accountsUsed = if (accountIdFilterList.isEmpty())
            allAccounts.let(::filterExcluded)
        else
            accountIdFilterList.mapNotNull { accID ->
                allAccounts.find { it.id == accID }
            }
        val accountIdFilterSet = accountsUsed.map { it.id }.toHashSet()

        Pair(accountsUsed, accountIdFilterSet)
    } then {
        val accountsUsed = it.first
        val accountIdFilterSet = it.second

        val transactions = trnsWithRangeAndAccFiltersAct(
            TrnsWithRangeAndAccFiltersAct.Input(
                range = range,
                accountIdFilterSet = accountIdFilterSet
            )
        )

        Pair(accountsUsed, transactions)
    } then {
        val accountsUsed = it.first
        val transactions = it.second

        val totalAmount = asyncIo {
            val incomeExpensePair = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = transactions,
                    accounts = accountsUsed,
                    baseCurrency = baseCurrency
                )
            )

            when (type) {
                TransactionType.INCOME -> incomeExpensePair.income.toDouble()
                TransactionType.EXPENSE -> incomeExpensePair.expense.toDouble()
                else -> error("not supported transactionType - $type")
            }
        }

        val categoryAmounts = asyncIo {
            val categories = categoriesAct(Unit)
            categories
                .plus(null) //for unspecified
                .map { category ->

                    val catIncomeExpense = categoryIncomeWithAccountFiltersAct(
                        CategoryIncomeWithAccountFiltersAct.Input(
                            transactions = transactions,
                            accountFilterList = accountsUsed,
                            category = category,
                            baseCurrency = baseCurrency
                        )
                    )

                    CategoryAmount(
                        category = category,
                        amount = when (type) {
                            TransactionType.INCOME -> catIncomeExpense.income.toDouble()
                            TransactionType.EXPENSE -> catIncomeExpense.expense.toDouble()
                            else -> error("not supported transactionType - $type")
                        }
                    )
                }
                .filter { catAmt ->
                    catAmt.amount != 0.0
                }
                .sortedByDescending { it.amount }
        }

        Output(totalAmount = totalAmount.await(), categoryAmounts = categoryAmounts.await())
    }

    data class Input(
        val baseCurrency: String,
        val range: FromToTimeRange,
        val type: TransactionType,
        val accountIdFilterList: List<UUID>
    )

    data class Output(val totalAmount: Double, val categoryAmounts: List<CategoryAmount>)
}