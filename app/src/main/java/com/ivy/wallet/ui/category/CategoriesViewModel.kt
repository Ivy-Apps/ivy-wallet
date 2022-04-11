package com.ivy.wallet.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.domain.logic.CategoryCreator
import com.ivy.wallet.domain.logic.WalletCategoryLogic
import com.ivy.wallet.domain.logic.model.CreateCategoryData
import com.ivy.wallet.domain.sync.item.CategorySync
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.utils.TestIdlingResource
import com.ivy.wallet.utils.asLiveData
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val categoryLogic: WalletCategoryLogic,
    private val categorySync: CategorySync,
    private val categoryCreator: CategoryCreator,
    private val ivyContext: IvyWalletCtx
) : ViewModel() {

    private val _currency = MutableLiveData<String>()
    val currency = _currency.asLiveData()

    private val _categories = MutableLiveData<List<CategoryData>>()
    val categories = _categories.asLiveData()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val range = TimePeriod.currentMonth(
                startDayOfMonth = ivyContext.startDayOfMonth
            ).toRange(ivyContext.startDayOfMonth) //this must be monthly

            _currency.value = ioThread { settingsDao.findFirst().currency }!!

            _categories.value = ioThread {
                categoryDao
                    .findAll()
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
                        categoryData.category.copy(
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