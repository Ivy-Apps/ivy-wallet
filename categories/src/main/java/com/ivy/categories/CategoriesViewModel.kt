package com.ivy.wallet.ui.category

import androidx.lifecycle.viewModelScope
import com.ivy.base.IvyWalletCtx
import com.ivy.base.SortOrder
import com.ivy.base.TimePeriod
import com.ivy.categories.CategoryData
import com.ivy.data.Account
import com.ivy.data.Category
import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.thenMap
import com.ivy.frp.test.TestIdlingResource
import com.ivy.frp.thenInvokeAfter
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.category.CategoryIncomeWithAccountFiltersAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.TrnsWithRangeAndAccFiltersAct
import com.ivy.wallet.domain.deprecated.logic.CategoryCreator
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.domain.deprecated.sync.item.CategorySync
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.data.toEntity
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.scopedIOThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categorySync: CategorySync,
    private val categoryCreator: CategoryCreator,
    private val categoriesAct: CategoriesAct,
    private val ivyContext: IvyWalletCtx,
    private val sharedPrefs: SharedPrefs,
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
                    accountIdFilterSet = suspend { allAccounts } thenMap { it.id }
                            thenInvokeAfter { it.toHashSet() }
                )
            )

            val sortOrder = SortOrder.from(
                sharedPrefs.getInt(
                    SharedPrefs.CATEGORY_SORT_ORDER,
                    SortOrder.DEFAULT.orderNum
                )
            )

            val parentCategoryList = categoryDao.findAllParentCategories().map { it.toDomain() }

            updateState {
                it.copy(sortOrder = sortOrder, parentCategoryList = parentCategoryList)
            }
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
            }

            val sortedList = sortList(categories, stateVal().sortOrder)

            updateState {
                it.copy(baseCurrency = baseCurrency, categories = sortedList)
            }
        }
    }

    private suspend fun reorder(
        newOrder: List<CategoryData>,
        sortOrder: SortOrder = SortOrder.DEFAULT
    ) {
        TestIdlingResource.increment()

        val sortedList = sortList(newOrder, sortOrder)

        if (sortOrder == SortOrder.DEFAULT) {
            ioThread {
                sortedList.forEachIndexed { index, categoryData ->
                    categoryDao.save(
                        categoryData.category.toEntity().copy(
                            orderNum = index.toDouble(),
                            isSynced = false
                        )
                    )
                }
            }
        }

        ioThread {
            sharedPrefs.putInt(SharedPrefs.CATEGORY_SORT_ORDER, sortOrder.orderNum)
        }

        updateState {
            it.copy(categories = sortedList, sortOrder = sortOrder)
        }

        ioThread {
            categorySync.sync()
        }

        TestIdlingResource.decrement()
    }

    private fun sortList(
        categoryData: List<CategoryData>,
        sortOrder: SortOrder
    ): List<CategoryData> {
        return when (sortOrder) {
            SortOrder.DEFAULT -> categoryData.sortedBy {
                it.category.orderNum
            }
            SortOrder.BALANCE_AMOUNT -> categoryData.sortedByDescending {
                it.monthlyBalance.absoluteValue
            }
            SortOrder.ALPHABETICAL -> categoryData.sortedBy {
                it.category.name
            }
            SortOrder.EXPENSES -> categoryData.sortedByDescending {
                it.monthlyExpenses
            }
        }
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
                is CategoriesScreenEvent.OnReorder -> reorder(event.newOrder, event.sortOrder)
                is CategoriesScreenEvent.OnCreateCategory -> createCategory(event.createCategoryData)
                is CategoriesScreenEvent.OnReorderModalVisible -> updateState {
                    it.copy(
                        reorderModalVisible = event.visible
                    )
                }
                is CategoriesScreenEvent.OnSortOrderModalVisible -> updateState {
                    it.copy(
                        sortModalVisible = event.visible
                    )
                }
                is CategoriesScreenEvent.OnCategoryModalVisible -> updateState {
                    it.copy(
                        categoryModalData = event.categoryModalData
                    )
                }
                else -> {}
            }
        }
    }
}

data class CategoriesScreenState(
    val baseCurrency: String = "",
    val categories: List<CategoryData> = emptyList(),
    val reorderModalVisible: Boolean = false,
    val categoryModalData: CategoryModalData? = null,
    val sortModalVisible: Boolean = false,
    val sortOrderItems: List<SortOrder> = SortOrder.values().toList(),
    val sortOrder: SortOrder = SortOrder.DEFAULT,
    val parentCategoryList: List<Category> = emptyList()
)

sealed class CategoriesScreenEvent {
    data class OnReorder(
        val newOrder: List<CategoryData>,
        val sortOrder: SortOrder = SortOrder.DEFAULT
    ) : CategoriesScreenEvent()

    data class OnCreateCategory(val createCategoryData: CreateCategoryData) :
        CategoriesScreenEvent()

    data class OnReorderModalVisible(val visible: Boolean) : CategoriesScreenEvent()
    data class OnSortOrderModalVisible(val visible: Boolean) : CategoriesScreenEvent()
    data class OnCategoryModalVisible(val categoryModalData: CategoryModalData?) :
        CategoriesScreenEvent()
}

suspend inline fun <T, R> Iterable<T>.mapAsync(
    scope: CoroutineScope,
    crossinline transform: suspend (T) -> R
): List<R> {
    return this.map {
        scope.async {
            transform(it)
        }
    }.awaitAll()
}
