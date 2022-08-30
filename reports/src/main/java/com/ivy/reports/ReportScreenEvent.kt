package com.ivy.reports

import android.content.Context
import android.net.Uri

sealed class ReportScreenEvent {

    object Start : ReportScreenEvent()

    data class FilterOptions(val visible: Boolean) : ReportScreenEvent()

    data class TransfersAsIncomeExpense(val transfersAsIncomeExpense: Boolean) :
        ReportScreenEvent()

    data class Export(val context: Context, val fileUri: Uri, val onFinish: (Uri) -> Unit) :
        ReportScreenEvent()

    data class FilterEvent(val filterEvent: ReportFilterEvent) : ReportScreenEvent()
}