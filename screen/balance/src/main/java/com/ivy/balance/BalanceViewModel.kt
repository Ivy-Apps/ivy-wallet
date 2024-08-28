package com.ivy.balance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.ui.ComposeViewModel
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.utils.ioThread
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val ivyContext: com.ivy.legacy.IvyWalletCtx,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val timeProvider: TimeProvider,
    private val timeConverter: TimeConverter,
) : ComposeViewModel<BalanceState, BalanceEvent>() {

    private val period = mutableStateOf(ivyContext.selectedPeriod)
    private val baseCurrencyCode = mutableStateOf("")
    private val currentBalance = mutableDoubleStateOf(0.0)
    private val plannedPaymentsAmount = mutableDoubleStateOf(0.0)
    private val balanceAfterPlannedPayments = mutableDoubleStateOf(0.0)
    private val numberOfMonthsAhead = mutableIntStateOf(1)

    @Composable
    override fun uiState(): BalanceState {
        LaunchedEffect(Unit) {
            start()
        }

        return BalanceState(
            period = period.value,
            balanceAfterPlannedPayments = balanceAfterPlannedPayments.doubleValue,
            currentBalance = currentBalance.doubleValue,
            baseCurrencyCode = baseCurrencyCode.value,
            plannedPaymentsAmount = plannedPaymentsAmount.doubleValue
        )
    }

    override fun onEvent(event: BalanceEvent) {
        when (event) {
            is BalanceEvent.OnNextMonth -> nextMonth()
            is BalanceEvent.OnSetPeriod -> setPeriod(event.timePeriod)
            is BalanceEvent.OnPreviousMonth -> previousMonth()
        }
    }

    private fun start(
        timePeriod: TimePeriod = ivyContext.selectedPeriod
    ) {
        viewModelScope.launch {
            baseCurrencyCode.value = baseCurrencyAct(Unit)
            period.value = timePeriod

            currentBalance.doubleValue = calcWalletBalanceAct(
                CalcWalletBalanceAct.Input(baseCurrencyCode.value)
            ).toDouble()

            plannedPaymentsAmount.doubleValue = ioThread {
                plannedPaymentsLogic.plannedPaymentsAmountFor(
                    timePeriod.toRange(ivyContext.startDayOfMonth, timeConverter, timeProvider)
                    // + positive if Income > Expenses else - negative
                ) * if (numberOfMonthsAhead.intValue >= 0) {
                    numberOfMonthsAhead.intValue.toDouble()
                } else {
                    1.0
                }
            }
            balanceAfterPlannedPayments.doubleValue =
                currentBalance.doubleValue + plannedPaymentsAmount.doubleValue
        }
    }

    private fun setPeriod(timePeriod: TimePeriod) {
        start(timePeriod = timePeriod)
    }

    private fun nextMonth() {
        val month = period.value.month
        val year = period.value.year ?: com.ivy.legacy.utils.dateNowUTC().year
        numberOfMonthsAhead.intValue += 1
        if (month != null) {
            start(
                timePeriod = month.incrementMonthPeriod(ivyContext, 1L, year = year)
            )
        }
    }

    private fun previousMonth() {
        val month = period.value.month
        val year = period.value.year ?: com.ivy.legacy.utils.dateNowUTC().year
        numberOfMonthsAhead.intValue -= 1
        if (month != null) {
            start(
                timePeriod = month.incrementMonthPeriod(ivyContext, -1L, year = year)
            )
        }
    }
}
