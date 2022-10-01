package com.ivy.reports.template.data

import com.ivy.data.transaction.TrnType
import com.ivy.reports.data.ReportPlannedPaymentType
import java.util.*

data class TemplateEntity(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val selectedTrnTypes: List<TrnType>,
    val templatePeriodRule: TemplatePeriodRule,
    val selectedAccounts: List<UUID>,
    val selectedCategories: List<UUID?>,
    val minAmount: Double?,
    val maxAmount: Double?,
    val includeKeywords: List<String>,
    val excludeKeywords: List<String>,
    val selectedPlannedPayments: List<ReportPlannedPaymentType>,
    val transfersAsIncomeExpense: Boolean
) {
    companion object {
        fun empty() = TemplateEntity(
            title = "",
            selectedTrnTypes = emptyList(),
            templatePeriodRule = TemplatePeriodRule.Custom,
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

