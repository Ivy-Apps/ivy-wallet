package com.ivy.reports

import android.content.Context
import com.ivy.data.model.Transaction

sealed class ReportScreenEvent {
    data class OnFilter(val filter: ReportFilter?) : ReportScreenEvent()
    data class OnExport(val context: Context) : ReportScreenEvent()
    data class OnPayOrGet(val transaction: Transaction) : ReportScreenEvent()
    data class SkipTransaction(val transaction: Transaction) : ReportScreenEvent()
    data class SkipTransactions(val transactions: List<Transaction>) : ReportScreenEvent()
    data class OnUpcomingExpanded(val upcomingExpanded: Boolean) : ReportScreenEvent()
    data class OnOverdueExpanded(val overdueExpanded: Boolean) : ReportScreenEvent()
    data class OnFilterOverlayVisible(val filterOverlayVisible: Boolean) : ReportScreenEvent()
    data class OnTagSearch(val data: String) : ReportScreenEvent()
    data class OnTreatTransfersAsIncomeExpense(val transfersAsIncomeExpense: Boolean) :
        ReportScreenEvent()

    @Deprecated("Uses legacy Transaction")
    data class SkipTransactionsLegacy(val transactions: List<com.ivy.base.legacy.Transaction>) :
        ReportScreenEvent()

    @Deprecated("Uses legacy Transaction")
    data class SkipTransactionLegacy(val transaction: com.ivy.base.legacy.Transaction) :
        ReportScreenEvent()

    @Deprecated("Uses legacy Transaction")
    data class OnPayOrGetLegacy(val transaction: com.ivy.base.legacy.Transaction) :
        ReportScreenEvent()
}
