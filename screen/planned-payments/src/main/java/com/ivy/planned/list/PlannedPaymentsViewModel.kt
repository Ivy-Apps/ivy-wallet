package com.ivy.planned.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.domain.ComposeViewModel
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.datamodel.PlannedPaymentRule
import com.ivy.legacy.utils.ioThread
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannedPaymentsViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val categoriesAct: CategoriesAct,
    private val accountsAct: AccountsAct
) : ComposeViewModel<PlannedPaymentsScreenState, PlannedPaymentsScreenEvent>() {

    private val currency = mutableStateOf("")
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val oneTimePlannedPayment =
        mutableStateOf<ImmutableList<PlannedPaymentRule>>(persistentListOf())
    private val recurringPlannedPayment =
        mutableStateOf<ImmutableList<PlannedPaymentRule>>(persistentListOf())
    private val oneTimeIncome = mutableDoubleStateOf(0.0)
    private val oneTimeExpenses = mutableDoubleStateOf(0.0)
    private val recurringIncome = mutableDoubleStateOf(0.0)
    private val recurringExpenses = mutableDoubleStateOf(0.0)
    private val isOneTimePaymentsExpanded = mutableStateOf(true)
    private val isRecurringPaymentsExpanded = mutableStateOf(true)

    @Composable
    override fun uiState(): PlannedPaymentsScreenState {
        LaunchedEffect(Unit) {
            start()
        }

        return PlannedPaymentsScreenState(
            currency = getCurrency(),
            categories = getCategories(),
            accounts = getAccounts(),
            oneTimeIncome = getOneTimeIncome(),
            oneTimeExpenses = getOneTimeExpenses(),
            recurringExpenses = getRecurringExpenses(),
            recurringIncome = getRecurringIncome(),
            recurringPlannedPayment = getRecurringPlannedPayment(),
            oneTimePlannedPayment = getOneTimePlannedPayment(),
            isOneTimePaymentsExpanded = getOneTimePaymentsExpanded(),
            isRecurringPaymentsExpanded = getRecurringPaymentsExpanded()
        )
    }

    @Composable
    private fun getCurrency(): String {
        return currency.value
    }

    @Composable
    private fun getCategories(): ImmutableList<Category> {
        return categories.value
    }

    @Composable
    private fun getAccounts(): ImmutableList<Account> {
        return accounts.value
    }

    @Composable
    private fun getOneTimePlannedPayment(): ImmutableList<PlannedPaymentRule> {
        return oneTimePlannedPayment.value
    }

    @Composable
    private fun getRecurringPlannedPayment(): ImmutableList<PlannedPaymentRule> {
        return recurringPlannedPayment.value
    }

    @Composable
    private fun getOneTimeExpenses(): Double {
        return oneTimeExpenses.doubleValue
    }

    @Composable
    private fun getOneTimeIncome(): Double {
        return oneTimeIncome.doubleValue
    }

    @Composable
    private fun getRecurringExpenses(): Double {
        return recurringExpenses.doubleValue
    }

    @Composable
    private fun getRecurringIncome(): Double {
        return recurringIncome.doubleValue
    }

    @Composable
    private fun getRecurringPaymentsExpanded(): Boolean {
        return isRecurringPaymentsExpanded.value
    }

    @Composable
    private fun getOneTimePaymentsExpanded(): Boolean {
        return isOneTimePaymentsExpanded.value
    }

    override fun onEvent(event: PlannedPaymentsScreenEvent) {
        when (event) {
            is PlannedPaymentsScreenEvent.OnOneTimePaymentsExpanded -> {
                isOneTimePaymentsExpanded.value = event.isExpanded
            }
            is PlannedPaymentsScreenEvent.OnRecurringPaymentsExpanded -> {
                isRecurringPaymentsExpanded.value = event.isExpanded
            }
        }
    }

    private fun start() {
        viewModelScope.launch {
            val settings = ioThread { settingsDao.findFirst() }
            currency.value = settings.currency

            categories.value = categoriesAct(Unit)
            accounts.value = accountsAct(Unit)

            oneTimePlannedPayment.value =
                ioThread { plannedPaymentsLogic.oneTime() }.toImmutableList()
            oneTimeIncome.doubleValue = ioThread { plannedPaymentsLogic.oneTimeIncome() }
            oneTimeExpenses.doubleValue = ioThread { plannedPaymentsLogic.oneTimeExpenses() }

            recurringPlannedPayment.value =
                ioThread { plannedPaymentsLogic.recurring() }.toImmutableList()
            recurringIncome.doubleValue = ioThread { plannedPaymentsLogic.recurringIncome() }
            recurringExpenses.doubleValue = ioThread { plannedPaymentsLogic.recurringExpenses() }
        }
    }
}
