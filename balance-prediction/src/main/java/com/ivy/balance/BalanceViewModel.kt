package com.ivy.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.wallet.domain.action.settings.BaseCurrencyActOld
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.utils.dateNowUTC
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.readOnly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val ivyContext: com.ivy.core.ui.temp.IvyWalletCtx,
    private val baseCurrencyAct: BaseCurrencyActOld,
    private val calcWalletBalanceAct: CalcWalletBalanceAct
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
            _baseCurrencyCode.value = baseCurrencyAct(Unit)

            _period.value = period

            val currentBalance = calcWalletBalanceAct(
                CalcWalletBalanceAct.Input(baseCurrencyCode.value)
            ).toDouble()

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