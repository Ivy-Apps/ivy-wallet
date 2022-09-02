package com.ivy.reports

import android.content.Context
import android.net.Uri
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TrnType

/** ---------------------------------- ReportScreen Main Events ----------------------------------*/

sealed class ReportsEvent {

    object Start : ReportsEvent()

    data class FilterOptions(val visible: Boolean) : ReportsEvent()

    data class TrnsAsIncomeExpense(val trnsAsIncExp: Boolean) : ReportsEvent()

    data class Export(val context: Context, val fileUri: Uri, val onFinish: (Uri) -> Unit) :
        ReportsEvent()

    data class FilterEvent(val filterEvent: ReportFilterEvent) : ReportsEvent()
}

/** -------------------------------- ReportScreen Filter Events ----------------------------------*/

sealed class ReportFilterEvent {

    data class SelectTrnsType(val type: TrnType, val checked: Boolean) : ReportFilterEvent()

    data class SelectPeriod(val timePeriod: TimePeriod) : ReportFilterEvent()

    data class SelectAccount(val account: Account, val add: Boolean) : ReportFilterEvent()

    data class SelectCategory(val category: Category, val add: Boolean) : ReportFilterEvent()

    data class SelectAmount(
        val amountType: AmountType,
        val amt: Double?
    ) : ReportFilterEvent() {
        enum class AmountType {
            MIN, MAX
        }
    }

    data class SelectKeyword(
        val keywordsType: KeywordsType,
        val keyword: String,
        val add: Boolean
    ) : ReportFilterEvent() {
        enum class KeywordsType {
            INCLUDE, EXCLUDE
        }
    }

    sealed class Clear : ReportFilterEvent() {
        object Accounts : Clear()
        object Categories : Clear()
        object Filter : Clear()
    }

    sealed class SelectAll : ReportFilterEvent() {
        object Accounts : SelectAll()
        object Categories : SelectAll()
    }

    data class FilterSet(val filter: FilterState) : ReportFilterEvent()
}