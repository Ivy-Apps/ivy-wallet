package com.ivy.reports

import android.content.Context
import android.net.Uri

sealed class ReportScreenEvent {
    object Start : ReportScreenEvent()

    data class OnFilterOverlayVisible(val filterOverlayVisible: Boolean) : ReportScreenEvent()
    data class OnFilter(val filter: ReportFilter?) : ReportScreenEvent()
    data class OnTransfersAsIncomeExpense(val transfersAsIncomeExpense: Boolean) :
        ReportScreenEvent()

    data class OnExport(val context: Context, val fileUri: Uri, val onFinish: (Uri) -> Unit) :
        ReportScreenEvent()
}