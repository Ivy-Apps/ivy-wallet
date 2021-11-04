package com.ivy.wallet.ui.balance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.asLiveData
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.PlannedPaymentsLogic
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val walletLogic: WalletLogic,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val ivyContext: IvyContext
) : ViewModel() {

    private val _period = MutableLiveData<TimePeriod>()
    val period = _period.asLiveData()

    private val _currency = MutableLiveData<String>()
    val currency = _currency.asLiveData()

    private val _currentBalance = MutableLiveData<Double>()
    val currentBalance = _currentBalance.asLiveData()

    private val _plannedPaymentsAmount = MutableLiveData<Double>()
    val plannedPaymentsAmount = _plannedPaymentsAmount.asLiveData()

    private val _balanceAfterPlannedPayments = MutableLiveData<Double>()
    val balanceAfterPlannedPayments = _balanceAfterPlannedPayments.asLiveData()

    fun start(period: TimePeriod = ivyContext.selectedPeriod) {
        viewModelScope.launch {
            val settings = ioThread { settingsDao.findFirst() }
            _currency.value = settings.currency

            _period.value = period

            val currentBalance = ioThread { walletLogic.calculateBalance() }!!
            _currentBalance.value = currentBalance

            val plannedPaymentsAmount = ioThread {
                plannedPaymentsLogic.plannedPaymentsAmountFor(period.toRange(ivyContext.startDateOfMonth)) //+ positive if Income > Expenses else - negative
            }
            _plannedPaymentsAmount.value = plannedPaymentsAmount

            _balanceAfterPlannedPayments.value = currentBalance + plannedPaymentsAmount
        }
    }

    fun setPeriod(period: TimePeriod) {
        start(period = period)
    }

    fun nextMonth() {
        val month = period.value?.month
        if (month != null) {
            start(
                period = month.incrementMonthPeriod(ivyContext, 1L),
            )
        }
    }

    fun previousMonth() {
        val month = period.value?.month
        if (month != null) {
            start(
                period = month.incrementMonthPeriod(ivyContext, -1L),
            )
        }
    }
}