package com.ivy.home

import com.ivy.base.TimePeriod
import com.ivy.data.transaction.Transaction
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

    data class PayOrGetPlanned(val transaction: Transaction) : HomeEvent()
    data class SkipPlanned(val transaction: Transaction) : HomeEvent()
    data class SkipAllPlanned(val transactions: List<Transaction>) : HomeEvent()

    data class DismissCustomerJourneyCard(val card: CustomerJourneyCardData) : HomeEvent()

    object SelectNextMonth : HomeEvent()
    object SelectPreviousMonth : HomeEvent()
}