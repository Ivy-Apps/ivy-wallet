package com.ivy.wallet.ui.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.base.readOnly
import com.ivy.wallet.functional.data.WalletDAOs
import com.ivy.wallet.functional.wallet.baseCurrencyCode
import com.ivy.wallet.functional.wallet.calculateWalletBalance
import com.ivy.wallet.logic.PlannedPaymentsLogic
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val walletDAOs: WalletDAOs,
    private val settingsDao: SettingsDao,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val ivyContext: IvyContext
) : ViewModel() {

    private val _period = MutableStateFlow(ivyContext.selectedPeriod)
    val period = _period.readOnly()

    private val _baseCurrencyCode = MutableStateFlow("")
    val baseCurrencyCode = _baseCurrencyCode.readOnly()

    private val _currentBalance = MutableStateFlow(0.0)
    val currentBalance = _currentBalance.readOnly()

    private val _plannedPaymentsAmount = MutableStateFlow(0.0)
    val plannedPaymentsAmount = _plannedPaymentsAmount.readOnly()

    private val _balanceAfterPlannedPayments = MutableStateFlow(0.0)
    val balanceAfterPlannedPayments = _balanceAfterPlannedPayments.readOnly()

    fun start(period: TimePeriod = ivyContext.selectedPeriod) {
        viewModelScope.launch {
            _baseCurrencyCode.value = ioThread { baseCurrencyCode(settingsDao) }

            _period.value = period

            val currentBalance = ioThread {
                calculateWalletBalance(
                    walletDAOs = walletDAOs,
                    baseCurrencyCode = baseCurrencyCode.value
                ).value.toDouble()
            }
            _currentBalance.value = currentBalance

            val plannedPaymentsAmount = ioThread {
                plannedPaymentsLogic.plannedPaymentsAmountFor(period.toRange(ivyContext.startDayOfMonth)) //+ positive if Income > Expenses else - negative
            }
            _plannedPaymentsAmount.value = plannedPaymentsAmount

            _balanceAfterPlannedPayments.value = currentBalance + plannedPaymentsAmount
        }
    }

    fun setPeriod(period: TimePeriod) {
        start(period = period)
    }

    fun nextMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            start(
                period = month.incrementMonthPeriod(ivyContext, 1L, year = year),
            )
        }
    }

    fun previousMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            start(
                period = month.incrementMonthPeriod(ivyContext, -1L, year = year),
            )
        }
    }
}