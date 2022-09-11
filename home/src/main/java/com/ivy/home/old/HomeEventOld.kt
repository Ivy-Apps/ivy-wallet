package com.ivy.home.old

import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.transaction.TransactionOld
import com.ivy.journey.domain.CustomerJourneyCardData

sealed class HomeEventOld {
    object Start : HomeEventOld()

    data class SetUpcomingExpanded(val expanded: Boolean) : HomeEventOld()
    data class SetOverdueExpanded(val expanded: Boolean) : HomeEventOld()

    object BalanceClick : HomeEventOld()
    object HiddenBalanceClick : HomeEventOld()

    object SwitchTheme : HomeEventOld()

    data class SetBuffer(val buffer: Double) : HomeEventOld()

    data class SetCurrency(val currency: String) : HomeEventOld()

    data class SetPeriod(val period: TimePeriod) : HomeEventOld()

    data class PayOrGetPlanned(val transaction: TransactionOld) : HomeEventOld()
    data class SkipPlanned(val transaction: TransactionOld) : HomeEventOld()
    data class SkipAllPlanned(val transactions: List<TransactionOld>) : HomeEventOld()

    data class DismissCustomerJourneyCard(val card: CustomerJourneyCardData) : HomeEventOld()

    object SelectNextMonth : HomeEventOld()
    object SelectPreviousMonth : HomeEventOld()
}