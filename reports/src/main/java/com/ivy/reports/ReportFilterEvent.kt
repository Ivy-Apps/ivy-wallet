package com.ivy.reports

import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TrnType

sealed class ReportFilterEvent {
    data class SelectTrnsType(val type: TrnType, val checked: Boolean) : ReportFilterEvent()
    data class SelectPeriod(val timePeriod: TimePeriod) : ReportFilterEvent()
    data class SelectAccount(val account: Account, val add: Boolean) : ReportFilterEvent()
    data class SelectCategory(val category: Category, val add: Boolean) : ReportFilterEvent()
    data class SelectAmount(val amountFilterType: AmountFilterType, val amt: Double?) :
        ReportFilterEvent()

    data class SelectKeyword(
        val keywordsFilterType: KeywordsFilterType,
        val keyword: String,
        val add: Boolean
    ) : ReportFilterEvent()

    data class Clear(val type: ClearType) : ReportFilterEvent()
    data class SelectAll(val type: SelectType) : ReportFilterEvent()
    data class FilterSet(val filter: ReportFilter) : ReportFilterEvent()
}

enum class ClearType {
    ALL, ACCOUNTS, CATEGORIES
}

enum class SelectType {
    ACCOUNTS, CATEGORIES
}

enum class KeywordsFilterType {
    INCLUDE, EXCLUDE
}

enum class AmountFilterType {
    MIN, MAX
}