package com.ivy.wallet.ui.reports

import android.content.Context
import com.ivy.wallet.domain.data.core.Transaction

sealed class ReportScreenEvent {
    data class OnFilter(val filter: ReportFilter?) : ReportScreenEvent()
    data class OnExport(val context: Context) : ReportScreenEvent()
    data class OnPayOrGet(val transaction: Transaction) : ReportScreenEvent()
    data class OnUpcomingExpanded(val upcomingExpanded: Boolean) : ReportScreenEvent()
    data class OnOverdueExpanded(val overdueExpanded: Boolean) : ReportScreenEvent()
    data class OnFilterOverlayVisible(val filterOverlayVisible: Boolean) : ReportScreenEvent()
    data class OnTreatTransfersAsIncomeExpense(val transfersAsIncomeExpense: Boolean) :
        ReportScreenEvent()
}