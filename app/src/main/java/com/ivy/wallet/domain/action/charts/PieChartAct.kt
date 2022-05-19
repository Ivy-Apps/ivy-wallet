package com.ivy.wallet.domain.action.charts

import androidx.compose.ui.graphics.toArgb
import com.ivy.frp.Pure
import com.ivy.frp.SideEffect
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenFilter
import com.ivy.frp.action.thenMap
import com.ivy.frp.then
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
                addAssociatedTransToCategoryAmt = existingTransactions.isNotEmpty()
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
        addAssociatedTransToCategoryAmt: Boolean = false,

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
                    TransactionType.INCOME -> catIncomeExpense.income.toDouble()
                    TransactionType.EXPENSE -> catIncomeExpense.expense.toDouble()
                    else -> error("not supported transactionType - $type")
                },
                associatedTransactions = categoryTransactions.await(),
                isCategoryUnspecified = category == null
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
        showAccountTransfersCategory: Boolean,
        type: TransactionType,
        accountTransfersCategory: Category,
        accountIdFilterSet: Set<UUID>,

        @SideEffect
        transactions: suspend () -> List<Transaction>,

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

                val amt = if (type == TransactionType.INCOME)
                    incExpQuad.transferIncome.toDouble()
                else
                    incExpQuad.transferExpense.toDouble()

                val categoryTrans = transactions().filter {
                    it.type == TransactionType.TRANSFER && it.categoryId == null
                }.filter {
                    if (type == TransactionType.EXPENSE)
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
        val type: TransactionType,
        val accountIdFilterList: List<UUID>,
        val treatTransferAsIncExp: Boolean = false,
        val showAccountTransfersCategory: Boolean = treatTransferAsIncExp,
        val existingTransactions: List<Transaction> = emptyList()
    )

    data class Output(val totalAmount: Double, val categoryAmounts: List<CategoryAmount>)
}