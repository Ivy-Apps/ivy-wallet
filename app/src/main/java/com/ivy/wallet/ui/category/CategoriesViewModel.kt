package com.ivy.wallet.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.deprecated.logic.CategoryCreator
import com.ivy.wallet.domain.deprecated.logic.WalletCategoryLogic
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.domain.deprecated.sync.item.CategorySync
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.utils.TestIdlingResource
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.readOnly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val categoryLogic: WalletCategoryLogic,
    private val categorySync: CategorySync,
    private val categoryCreator: CategoryCreator,
    private val categoriesAct: CategoriesAct,
    private val ivyContext: IvyWalletCtx,
    private val baseCurrencyAct: BaseCurrencyAct
) : ViewModel() {

    private val _currency = MutableStateFlow("")
    val currency = _currency.readOnly()

    private val _categories = MutableStateFlow<List<CategoryData>>(emptyList())
    val categories = _categories.readOnly()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val range = TimePeriod.currentMonth(
                startDayOfMonth = ivyContext.startDayOfMonth
            ).toRange(ivyContext.startDayOfMonth) //this must be monthly

            _currency.value = baseCurrencyAct(Unit)

            _categories.value = ioThread {
                categoriesAct(Unit)
                    .map {
                        CategoryData(
                            category = it,
                            monthlyBalance = categoryLogic.calculateCategoryBalance(
                                it,
                                range
                            ),
                            monthlyIncome = categoryLogic.calculateCategoryIncome(
                                category = it,
                                range = range
                            ),
                            monthlyExpenses = categoryLogic.calculateCategoryExpenses(
                                category = it,
                                range = range
                            ),
                        )
                    }
            }!!

            TestIdlingResource.decrement()
        }
    }

    fun reorder(newOrder: List<CategoryData>) {
        viewModelScope.launch {
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
            start()

            ioThread {
                categorySync.sync()
            }

            TestIdlingResource.decrement()
        }
    }

    fun createCategory(data: CreateCategoryData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            categoryCreator.createCategory(data) {
                start()
            }

            TestIdlingResource.decrement()
        }
    }
}