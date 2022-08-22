package com.ivy.reports

import android.content.Context

sealed class ReportScreenEvent {
    data class OnExport(val context: Context) : ReportScreenEvent()
    object Start : ReportScreenEvent()
    data class OnUpcomingExpanded(val upcomingExpanded: Boolean) : ReportScreenEvent()
    data class OnOverdueExpanded(val overdueExpanded: Boolean) : ReportScreenEvent()
    data class OnFilterOverlayVisible(val filterOverlayVisible: Boolean) : ReportScreenEvent()
    data class OnFilter(val filter: ReportFilter?) : ReportScreenEvent()
    data class OnTransfersAsIncomeExpense(val transfersAsIncomeExpense: Boolean) :
        ReportScreenEvent()
}