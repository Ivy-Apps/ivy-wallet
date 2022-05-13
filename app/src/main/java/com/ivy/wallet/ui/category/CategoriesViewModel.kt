package com.ivy.wallet.ui.category

import androidx.lifecycle.viewModelScope
import com.ivy.fp.action.mapAsync
import com.ivy.fp.action.thenFinishWith
import com.ivy.fp.action.thenMap
import com.ivy.fp.viewmodel.FRPViewModel
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.category.CategoryIncomeWithAccountFiltersAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.TrnsWithRangeAndAccFiltersAct
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.deprecated.logic.CategoryCreator
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.domain.deprecated.sync.item.CategorySync
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import com.ivy.wallet.utils.TestIdlingResource
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.scopedIOThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categorySync: CategorySync,
    private val categoryCreator: CategoryCreator,
    private val categoriesAct: CategoriesAct,
    private val ivyContext: IvyWalletCtx,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val accountsAct: AccountsAct,
    private val trnsWithRangeAndAccFiltersAct: TrnsWithRangeAndAccFiltersAct,
    private val categoryIncomeWithAccountFiltersAct: CategoryIncomeWithAccountFiltersAct
) : FRPViewModel<CategoriesScreenState, Nothing>() {

    override val _state: MutableStateFlow<CategoriesScreenState> = MutableStateFlow(
        CategoriesScreenState()
    )

    override suspend fun handleEvent(event: Nothing): suspend () -> CategoriesScreenState {
        TODO("Not yet implemented")
    }

    private var allAccounts = emptyList<Account>()
    private var baseCurrency = ""
    private var transactions = emptyList<Transaction>()

    fun start() {
        viewModelScope.launch(Dispatchers.IO) {
            TestIdlingResource.increment()

            initialise()
            loadCategories()

            TestIdlingResource.decrement()
        }
    }

    private suspend fun initialise() {
        ioThread {
            val range = TimePeriod.currentMonth(
                startDayOfMonth = ivyContext.startDayOfMonth
            ).toRange(ivyContext.startDayOfMonth) //this must be monthly

            allAccounts = accountsAct(Unit)
            baseCurrency = baseCurrencyAct(Unit)

            transactions = trnsWithRangeAndAccFiltersAct(
                TrnsWithRangeAndAccFiltersAct.Input(
                    range = range,
                    accountIdFilterSet = suspend { allAccounts } thenMap { it.id } thenFinishWith { it.toHashSet() }
                )
            )
        }
    }

    private suspend fun loadCategories() {
        scopedIOThread { scope ->
            val categories = categoriesAct(Unit).mapAsync(scope) {
                val catIncomeExpense = categoryIncomeWithAccountFiltersAct(
                    CategoryIncomeWithAccountFiltersAct.Input(
                        transactions = transactions,
                        accountFilterList = allAccounts,
                        category = it,
                        baseCurrency = baseCurrency
                    )
                )

                CategoryData(
                    category = it,
                    monthlyBalance = (catIncomeExpense.income - catIncomeExpense.expense).toDouble(),
                    monthlyIncome = catIncomeExpense.income.toDouble(),
                    monthlyExpenses = catIncomeExpense.expense.toDouble()
                )
            }.sortedBy {
                it.category.orderNum
            }

            updateState {
                it.copy(baseCurrency = baseCurrency, categories = categories)
            }
        }
    }

    private suspend fun reorder(newOrder: List<CategoryData>) {
        TestIdlingResource.increment()

        ioThread {
            newOrder.forEachIndexed { index, categoryData ->
                categoryDao.save(
                    categoryData.category.toEntity().copy(
                        orderNum = index.toDouble(),
                        isSynced = false
                    )
                )
            }
        }

        updateState {
            it.copy(categories = newOrder)
        }

        ioThread {
            categorySync.sync()
        }

        TestIdlingResource.decrement()
    }

    private suspend fun createCategory(data: CreateCategoryData) {
        TestIdlingResource.increment()

        categoryCreator.createCategory(data) {
            loadCategories()
        }

        TestIdlingResource.decrement()
    }

    fun onEvent(event: CategoriesScreenEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is CategoriesScreenEvent.OnReorder -> reorder(event.newOrder)
                is CategoriesScreenEvent.OnCreateCategory -> createCategory(event.createCategoryData)
                is CategoriesScreenEvent.OnReorderModalVisible -> updateState {
                    it.copy(
                        reorderModalVisible = event.visible
                    )
                }
                is CategoriesScreenEvent.OnCategoryModalVisible -> updateState {
                    it.copy(
                        categoryModalData = event.categoryModalData
                    )
                }
            }
        }
    }
}

data class CategoriesScreenState(
    val baseCurrency: String = "",
    val categories: List<CategoryData> = emptyList(),
    val reorderModalVisible: Boolean = false,
    val categoryModalData: CategoryModalData? = null
)

sealed class CategoriesScreenEvent {
    data class OnReorder(val newOrder: List<CategoryData>) : CategoriesScreenEvent()
    data class OnCreateCategory(val createCategoryData: CreateCategoryData) :
        CategoriesScreenEvent()

    data class OnReorderModalVisible(val visible: Boolean) : CategoriesScreenEvent()
    data class OnCategoryModalVisible(val categoryModalData: CategoryModalData?) :
        CategoriesScreenEvent()
}
