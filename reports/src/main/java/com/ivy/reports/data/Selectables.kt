package com.ivy.reports.data

import com.ivy.data.account.Account

data class SelectableAccount(val account: Account, val selected: Boolean = false)

data class SelectableReportsCategory(
    val selectableCategory: ReportCategoryType,
    val selected: Boolean = false
)