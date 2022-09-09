package com.ivy.reports.data

import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.transaction.TrnType

data class ReportFilterState(
    val selectedTrnTypes: List<TrnType>,

    val period: TimePeriod?,

    val selectedAccounts: List<SelectableAccount>,

    val selectedCategories: List<SelectableReportsCategory>,

    val minAmount: Double?,
    val maxAmount: Double?,

    val includeKeywords: List<String>,
    val excludeKeywords: List<String>,

    val selectedPlannedPayments: List<ReportPlannedPaymentType>,

    val transfersAsIncomeExpense: Boolean
) {
    companion object {
        fun empty() = ReportFilterState(
            selectedTrnTypes = emptyList(),
            period = null,
            selectedAccounts = emptyList(),
            selectedCategories = emptyList(),

            minAmount = null,
            maxAmount = null,

            includeKeywords = emptyList(),
            excludeKeywords = emptyList(),

            selectedPlannedPayments = emptyList(),
            transfersAsIncomeExpense = false
        )
    }
}
