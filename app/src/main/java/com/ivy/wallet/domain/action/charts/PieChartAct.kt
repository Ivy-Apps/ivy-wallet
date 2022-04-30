package com.ivy.wallet.domain.action.charts

import androidx.compose.ui.graphics.toArgb
import com.ivy.fp.Pure
import com.ivy.fp.SideEffect
import com.ivy.fp.action.FPAction
import com.ivy.fp.action.then
import com.ivy.fp.action.thenFilter
import com.ivy.fp.action.thenMap
import com.ivy.wallet.R
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.category.CategoryIncomeWithAccountFiltersAct
import com.ivy.wallet.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.ivy.wallet.domain.action.transaction.TrnsWithRangeAndAccFiltersAct
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.account.filterExcluded
import com.ivy.wallet.domain.pure.data.IncomeExpenseTransferPair
import com.ivy.wallet.stringRes
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.statistic.level1.CategoryAmount
import com.ivy.wallet.ui.theme.RedLight
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class PieChartAct @Inject constructor(
    private val accountsAct: AccountsAct,
    private val trnsWithRangeAndAccFiltersAct: TrnsWithRangeAndAccFiltersAct,
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct,
    private val categoriesAct: CategoriesAct,
    private val categoryIncomeWithAccountFiltersAct: CategoryIncomeWithAccountFiltersAct
) : FPAction<PieChartAct.Input, PieChartAct.Output>() {

    private val accountTransfersCategory =
        Category(stringRes(R.string.account_transfers), RedLight.toArgb(), "transfer")

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        getUsableAccounts(
            accountIdFilterList = accountIdFilterList,
            allAccounts = suspend { accountsAct(Unit) }
        )
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

        val incomeExpenseTransfer = calcTrnsIncomeExpenseAct(
            CalcTrnsIncomeExpenseAct.Input(
                transactions = transactions,
                accounts = accountsUsed,
                baseCurrency = baseCurrency
            )
        )

        val categoryAmounts = calculateCategoryAmounts(
            type = type,
            baseCurrency = baseCurrency,
            allCategories = suspend {
                categoriesAct(Unit).plus(null) //for unspecified
            },
            transactions = suspend { transactions },
            accountsUsed = suspend { accountsUsed }
        )

        Pair(incomeExpenseTransfer, categoryAmounts)
    } then {

        val totalAmount = calculateTotalAmount(
            type = type,
            treatTransferAsIncExp = treatTransferAsIncExp,
            incomeExpenseTransfer = suspend { it.first }
        )

        val catAmountList = addAccountTransfersCategory(
            treatTransferAsIncExp = treatTransferAsIncExp,
            type = type,
            incomeExpenseTransfer = suspend { it.first },
            accountTransfersCategory = accountTransfersCategory,
            categoryAmounts = suspend { it.second }
        )

        Pair(totalAmount, catAmountList)
    } then {
        Output(it.first.toDouble(), it.second)
    }

    @Pure
    private suspend fun getUsableAccounts(
        accountIdFilterList: List<UUID>,

        @SideEffect
        allAccounts: suspend () -> List<Account>
    ): Pair<List<Account>, Set<UUID>> {

        val accountsUsed = if (accountIdFilterList.isEmpty())
            allAccounts then ::filterExcluded
        else
            allAccounts thenFilter {
                accountIdFilterList.contains(it.id)
            }

        val accountsUsedIDSet = accountsUsed thenMap { it.id } then { it.toHashSet() }

        return Pair(accountsUsed(), accountsUsedIDSet())
    }

    @Pure
    private suspend fun calculateCategoryAmounts(
        type: TransactionType,
        baseCurrency: String,

        @SideEffect
        allCategories: suspend () -> List<Category?>,

        @SideEffect
        transactions: suspend () -> List<Transaction>,

        @SideEffect
        accountsUsed: suspend () -> List<Account>,
    ): List<CategoryAmount> {
        val trans = transactions()
        val accUsed = accountsUsed()

        val catAmtList = allCategories thenMap { category ->
            val catIncomeExpense = categoryIncomeWithAccountFiltersAct(
                CategoryIncomeWithAccountFiltersAct.Input(
                    transactions = trans,
                    accountFilterList = accUsed,
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
        } thenFilter { catAmt ->
            catAmt.amount != 0.0
        } then {
            it.sortedByDescending { ca -> ca.amount }
        }

        return catAmtList()
    }

    @Pure
    private suspend fun calculateTotalAmount(
        type: TransactionType,
        treatTransferAsIncExp: Boolean,

        @SideEffect
        incomeExpenseTransfer: suspend () -> IncomeExpenseTransferPair
    ): BigDecimal {
        val incExpQuad = incomeExpenseTransfer()
        return when (type) {
            TransactionType.INCOME -> {
                incExpQuad.income +
                        if (treatTransferAsIncExp)
                            incExpQuad.transferIncome
                        else
                            BigDecimal.ZERO
            }
            TransactionType.EXPENSE -> {
                incExpQuad.expense +
                        if (treatTransferAsIncExp)
                            incExpQuad.transferExpense
                        else
                            BigDecimal.ZERO
            }
            else -> BigDecimal.ZERO
        }
    }

    @Pure
    private suspend fun addAccountTransfersCategory(
        treatTransferAsIncExp: Boolean,
        type: TransactionType,
        accountTransfersCategory: Category,

        @SideEffect
        incomeExpenseTransfer: suspend () -> IncomeExpenseTransferPair,

        @SideEffect
        categoryAmounts: suspend () -> List<CategoryAmount>
    ): List<CategoryAmount> {

        val incExpQuad = incomeExpenseTransfer()

        val catAmtList =
            if (!treatTransferAsIncExp || incExpQuad.transferIncome == BigDecimal.ZERO && incExpQuad.transferExpense == BigDecimal.ZERO)
                categoryAmounts then { it.sortedByDescending { ca -> ca.amount } }
            else {

                val amt = if (type == TransactionType.INCOME)
                    incExpQuad.transferIncome.toDouble()
                else
                    incExpQuad.transferExpense.toDouble()


                categoryAmounts then {
                    it.plus(
                        CategoryAmount(accountTransfersCategory, amt)
                    )
                } then {
                    it.sortedByDescending { ca -> ca.amount }
                }
            }

        return catAmtList()
    }

    data class Input(
        val baseCurrency: String,
        val range: FromToTimeRange,
        val type: TransactionType,
        val accountIdFilterList: List<UUID>,
        val treatTransferAsIncExp: Boolean = false
    )

    data class Output(val totalAmount: Double, val categoryAmounts: List<CategoryAmount>)
}