package com.ivy.pie_charts.action

import androidx.compose.ui.graphics.toArgb
import com.ivy.base.FromToTimeRange
import com.ivy.base.R
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnType
import com.ivy.design.l0_system.RedLight
import com.ivy.frp.Pure
import com.ivy.frp.SideEffect
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenFilter
import com.ivy.frp.action.thenMap
import com.ivy.frp.then
import com.ivy.pie_charts.model.CategoryAmount
import com.ivy.wallet.domain.action.account.AccountsActOld
import com.ivy.wallet.domain.action.category.CategoriesActOld
import com.ivy.wallet.domain.action.category.CategoryIncomeWithAccountFiltersAct
import com.ivy.wallet.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.ivy.wallet.domain.action.transaction.TrnsWithRangeAndAccFiltersAct
import com.ivy.wallet.domain.pure.account.filterExcluded
import com.ivy.wallet.domain.pure.data.IncomeExpenseTransferPair
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class PieChartAct @Inject constructor(
    private val accountsAct: AccountsActOld,
    private val trnsWithRangeAndAccFiltersAct: TrnsWithRangeAndAccFiltersAct,
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct,
    private val categoriesAct: CategoriesActOld,
    private val categoryIncomeWithAccountFiltersAct: CategoryIncomeWithAccountFiltersAct
) : FPAction<PieChartAct.Input, PieChartAct.Output>() {

    private val accountTransfersCategory =
        CategoryOld(
            com.ivy.core.ui.temp.stringRes(R.string.account_transfers),
            RedLight.toArgb(),
            "transfer"
        )

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        getUsableAccounts(
            accountIdFilterList = accountIdFilterList,
            allAccounts = suspend { accountsAct(Unit) }
        )
    } then {
        val accountsUsed = it.first
        val accountIdFilterSet = it.second

        val transactions = existingTransactions.ifEmpty {
            trnsWithRangeAndAccFiltersAct(
                TrnsWithRangeAndAccFiltersAct.Input(
                    range = range,
                    accountIdFilterSet = accountIdFilterSet
                )
            )
        }

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

        val categoryAmounts = suspend {
            calculateCategoryAmounts(
                type = type,
                baseCurrency = baseCurrency,
                allCategories = suspend {
                    categoriesAct(Unit).plus(null) //for unspecified
                },
                transactions = suspend { transactions },
                accountsUsed = suspend { accountsUsed },
                addAssociatedTransToCategoryAmt = existingTransactions.isNotEmpty(),
                filterEmptyCategoryAmounts = filterEmptyCategoryAmounts
            )
        } then {
            addAccountTransfersCategory(
                showAccountTransfersCategory = showAccountTransfersCategory,
                type = type,
                accountTransfersCategory = accountTransfersCategory,
                accountIdFilterSet = accountIdFilterList.toHashSet(),
                incomeExpenseTransfer = suspend { incomeExpenseTransfer },
                categoryAmounts = suspend { it },
                transactions = suspend { transactions }
            )
        }

        Pair(incomeExpenseTransfer, categoryAmounts())
    } then {

        val totalAmount = calculateTotalAmount(
            type = type,
            treatTransferAsIncExp = treatTransferAsIncExp,
            incomeExpenseTransfer = suspend { it.first }
        )

        val catAmountList = it.second

        Pair(totalAmount, catAmountList)
    } then {
        Output(it.first.toDouble(), it.second)
    }

    @Pure
    private suspend fun getUsableAccounts(
        accountIdFilterList: List<UUID>,

        @SideEffect
        allAccounts: suspend () -> List<AccountOld>
    ): Pair<List<AccountOld>, Set<UUID>> {

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
        type: TrnType,
        baseCurrency: String,
        addAssociatedTransToCategoryAmt: Boolean = false,
        filterEmptyCategoryAmounts: Boolean = true,

        @SideEffect
        allCategories: suspend () -> List<CategoryOld?>,

        @SideEffect
        transactions: suspend () -> List<TransactionOld>,

        @SideEffect
        accountsUsed: suspend () -> List<AccountOld>,
    ): List<CategoryAmount> {
        val trans = transactions()
        val accUsed = accountsUsed()

        val catAmtList = allCategories thenMap { category ->
            val categoryTransactions = asyncIo {
                if (addAssociatedTransToCategoryAmt)
                    trans.filter {
                        it.type == type && it.categoryId == category?.id
                    }
                else
                    emptyList()
            }

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
                    TrnType.INCOME -> catIncomeExpense.income.toDouble()
                    TrnType.EXPENSE -> catIncomeExpense.expense.toDouble()
                    else -> error("not supported transactionType - $type")
                },
                associatedTransactions = categoryTransactions.await(),
                isCategoryUnspecified = category == null
            )
        } thenFilter { catAmt ->
            if (filterEmptyCategoryAmounts)
                catAmt.amount != 0.0
            else
                true
        } then {
            it.sortedByDescending { ca -> ca.amount }
        }

        return catAmtList()
    }

    @Pure
    private suspend fun calculateTotalAmount(
        type: TrnType,
        treatTransferAsIncExp: Boolean,

        @SideEffect
        incomeExpenseTransfer: suspend () -> IncomeExpenseTransferPair
    ): BigDecimal {
        val incExpQuad = incomeExpenseTransfer()
        return when (type) {
            TrnType.INCOME -> {
                incExpQuad.income +
                        if (treatTransferAsIncExp)
                            incExpQuad.transferIncome
                        else
                            BigDecimal.ZERO
            }
            TrnType.EXPENSE -> {
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
        showAccountTransfersCategory: Boolean,
        type: TrnType,
        accountTransfersCategory: CategoryOld,
        accountIdFilterSet: Set<UUID>,

        @SideEffect
        transactions: suspend () -> List<TransactionOld>,

        @SideEffect
        incomeExpenseTransfer: suspend () -> IncomeExpenseTransferPair,

        @SideEffect
        categoryAmounts: suspend () -> List<CategoryAmount>
    ): List<CategoryAmount> {

        val incExpQuad = incomeExpenseTransfer()

        val catAmtList =
            if (!showAccountTransfersCategory || incExpQuad.transferIncome == BigDecimal.ZERO && incExpQuad.transferExpense == BigDecimal.ZERO)
                categoryAmounts then { it.sortedByDescending { ca -> ca.amount } }
            else {

                val amt = if (type == TrnType.INCOME)
                    incExpQuad.transferIncome.toDouble()
                else
                    incExpQuad.transferExpense.toDouble()

                val categoryTrans = transactions().filter {
                    it.type == TrnType.TRANSFER && it.categoryId == null
                }.filter {
                    if (type == TrnType.EXPENSE)
                        accountIdFilterSet.contains(it.accountId)
                    else
                        accountIdFilterSet.contains(it.toAccountId)
                }

                categoryAmounts then {
                    it.plus(
                        CategoryAmount(
                            category = accountTransfersCategory,
                            amount = amt,
                            associatedTransactions = categoryTrans,
                            isCategoryUnspecified = true
                        )
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
        val type: TrnType,
        val accountIdFilterList: List<UUID>,
        val treatTransferAsIncExp: Boolean = false,
        val showAccountTransfersCategory: Boolean = treatTransferAsIncExp,
        val existingTransactions: List<TransactionOld> = emptyList(),
        val filterEmptyCategoryAmounts: Boolean = true
    )

    data class Output(val totalAmount: Double, val categoryAmounts: List<CategoryAmount>)
}