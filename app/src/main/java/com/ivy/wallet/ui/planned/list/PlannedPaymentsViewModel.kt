package com.ivy.wallet.ui.planned.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.PlannedPaymentRule
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.PlannedPayments
import com.ivy.wallet.utils.asLiveData
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannedPaymentsViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val categoriesAct: CategoriesAct,
    private val accountsAct: AccountsAct
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

            _categories.value = categoriesAct(Unit)!!
            _accounts.value = accountsAct(Unit)!!

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