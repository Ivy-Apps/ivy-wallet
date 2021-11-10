package com.ivy.wallet.ui.statistic.level1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.asLiveData
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.WalletCategoryLogic
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class PieChartStatisticViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val walletLogic: WalletLogic,
    private val settingsDao: SettingsDao,
    private val categoryLogic: WalletCategoryLogic,
    private val ivyContext: IvyContext
) : ViewModel() {
    private val _period = MutableLiveData<TimePeriod>()
    val period = _period.asLiveData()

    private val _type = MutableLiveData<TransactionType>()
    val type = _type.asLiveData()

    private val _currency = MutableLiveData<String>()
    val currency = _currency.asLiveData()

    private val _totalAmount = MutableLiveData<Double>()
    val totalAmount = _totalAmount.asLiveData()

    private val _categoryAmounts = MutableLiveData<List<CategoryAmount>>()
    val categoryAmounts = _categoryAmounts.asLiveData()

    private val _selectedCategory = MutableLiveData<SelectedCategory?>()
    val selectedCategory = _selectedCategory.asLiveData()

    fun start(
        screen: Screen.PieChartStatistic
    ) {
        load(
            period = ivyContext.selectedPeriod,
            type = screen.type
        )
    }

    private fun load(
        period: TimePeriod,
        type: TransactionType
    ) {
        _period.value = period
        val range = period.toRange(ivyContext.startDayOfMonth)
        _type.value = type

        _selectedCategory.value = null

        viewModelScope.launch {
            val settings = ioThread { settingsDao.findFirst() }

            _currency.value = settings.currency

            _totalAmount.value = ioThread {
                when (type) {
                    TransactionType.INCOME -> walletLogic.calculateIncome(range)
                    TransactionType.EXPENSE -> walletLogic.calculateExpenses(range)
                    else -> error("not supported transactionType - $type")
                }
            }.absoluteValue

            _categoryAmounts.value = ioThread {
                categoryDao
                    .findAll()
                    .map {
                        CategoryAmount(
                            category = it,
                            amount = when (type) {
                                TransactionType.INCOME -> categoryLogic.calculateCategoryIncome(
                                    category = it,
                                    range = range
                                )
                                TransactionType.EXPENSE -> categoryLogic.calculateCategoryExpenses(
                                    category = it,
                                    range = range
                                )
                                else -> error("not supported transactionType - $type")
                            }
                        )
                    }
                    .plus(
                        //Unspecified
                        CategoryAmount(
                            category = null,
                            amount = when (type) {
                                TransactionType.INCOME -> categoryLogic.calculateUnspecifiedIncome(
                                    range = range
                                )
                                TransactionType.EXPENSE -> categoryLogic.calculateUnspecifiedExpenses(
                                    range = range
                                )
                                else -> error("not supported transactionType - $type")
                            }
                        )
                    )
                    .sortedByDescending { it.amount }
            }!!
        }
    }

    fun setSelectedCategory(selectedCategory: SelectedCategory?) {
        _selectedCategory.value = selectedCategory

        val categoryAmounts = _categoryAmounts.value ?: return
        _categoryAmounts.value = if (selectedCategory != null) {
            categoryAmounts
                .sortedByDescending { it.amount }
                .sortedByDescending {
                    selectedCategory.category == it.category
                }
        } else {
            categoryAmounts.sortedByDescending {
                it.amount
            }
        }
    }

    fun onSetPeriod(period: TimePeriod) {
        ivyContext.updateSelectedPeriodInMemory(period)
        load(
            period = period,
            type = type.value!!
        )
    }
}