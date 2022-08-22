package com.ivy.home

import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.transaction.TransactionOld
import com.ivy.journey.domain.CustomerJourneyCardData

sealed class HomeEvent {
    object Start : HomeEvent()

    data class SetUpcomingExpanded(val expanded: Boolean) : HomeEvent()
    data class SetOverdueExpanded(val expanded: Boolean) : HomeEvent()

    object BalanceClick : HomeEvent()
    object HiddenBalanceClick : HomeEvent()

    object SwitchTheme : HomeEvent()

    data class SetBuffer(val buffer: Double) : HomeEvent()

    data class SetCurrency(val currency: String) : HomeEvent()

    data class SetPeriod(val period: TimePeriod) : HomeEvent()

    data class PayOrGetPlanned(val transaction: TransactionOld) : HomeEvent()
    data class SkipPlanned(val transaction: TransactionOld) : HomeEvent()
    data class SkipAllPlanned(val transactions: List<TransactionOld>) : HomeEvent()

    data class DismissCustomerJourneyCard(val card: CustomerJourneyCardData) : HomeEvent()

    object SelectNextMonth : HomeEvent()
    object SelectPreviousMonth : HomeEvent()
}