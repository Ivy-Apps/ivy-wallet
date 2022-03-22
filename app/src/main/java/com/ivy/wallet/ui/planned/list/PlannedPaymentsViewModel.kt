package com.ivy.wallet.ui.planned.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.asLiveData
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.PlannedPaymentsLogic
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.PlannedPaymentRule
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.persistence.dao.PlannedPaymentRuleDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.ui.PlannedPayments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannedPaymentsViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val plannedPaymentsLogic: PlannedPaymentsLogic
) : ViewModel() {

    private val _currency = MutableLiveData<String>()
    val currency = _currency.asLiveData()

    private val _categories = MutableLiveData<List<Category>>()
    val categories = _categories.asLiveData()

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts = _accounts.asLiveData()

    //One Time
    private val _oneTime = MutableLiveData<List<PlannedPaymentRule>>()
    val oneTime = _oneTime.asLiveData()

    private val _oneTimeIncome = MutableLiveData<Double>()
    val oneTimeIncome = _oneTimeIncome.asLiveData()

    private val _oneTimeExpenses = MutableLiveData<Double>()
    val oneTimeExpenses = _oneTimeExpenses.asLiveData()

    //Recurring
    private val _recurring = MutableLiveData<List<PlannedPaymentRule>>()
    val recurring = _recurring.asLiveData()

    private val _recurringIncome = MutableLiveData<Double>()
    val recurringIncome = _recurringIncome.asLiveData()

    private val _recurringExpenses = MutableLiveData<Double>()
    val recurringExpenses = _recurringExpenses.asLiveData()

    fun start(screen: PlannedPayments) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val settings = ioThread { settingsDao.findFirst() }

            _currency.value = settings.currency

            _categories.value = ioThread { categoryDao.findAll() }!!
            _accounts.value = ioThread { accountDao.findAll() }!!

            _oneTime.value = ioThread { plannedPaymentsLogic.oneTime() }!!
            _oneTimeIncome.value = ioThread { plannedPaymentsLogic.oneTimeIncome() }!!
            _oneTimeExpenses.value = ioThread { plannedPaymentsLogic.oneTimeExpenses() }!!

            _recurring.value = ioThread { plannedPaymentsLogic.recurring() }!!
            _recurringIncome.value = ioThread { plannedPaymentsLogic.recurringIncome() }!!
            _recurringExpenses.value = ioThread { plannedPaymentsLogic.recurringExpenses() }!!

            TestIdlingResource.decrement()
        }
    }
}