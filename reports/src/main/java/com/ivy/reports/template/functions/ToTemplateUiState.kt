package com.ivy.reports.template.functions

import com.ivy.reports.template.data.Template
import com.ivy.reports.template.data.TemplateLabels
import com.ivy.reports.template.data.TemplatePeriodRule
import com.ivy.reports.template.ui.TemplateUiState
import com.ivy.wallet.utils.capitalizeLocal
import com.ivy.wallet.utils.isNotNullOrEmpty
import com.ivy.wallet.utils.toLowerCaseLocal

fun Template.toUiState(): TemplateUiState {
    return TemplateUiState(
        title = "",
        accounts = this.selectedAccounts,
        categories = this.selectedCategories,
        compulsoryContent = this.toCompulsoryContent(),
        optionalContent = this.toOptionalContent()
    )
}

private fun Template.toCompulsoryContent(): Map<TemplateLabels, String> {
    val content = HashMap<TemplateLabels, String>()

    val typeString =
        this.selectedTrnTypes.joinToString(", ") {
            it.name.toLowerCaseLocal().capitalizeLocal()
        }

    val periodString = when (this.templatePeriodRule) {
        is TemplatePeriodRule.AllTime -> "All Time"
        is TemplatePeriodRule.Custom -> "Custom"
        is TemplatePeriodRule.LastN -> "Previous ${this.templatePeriodRule.lastN}\t ${this.templatePeriodRule.intervalType.name}s"
        is TemplatePeriodRule.Month -> when (this.templatePeriodRule) {
            is TemplatePeriodRule.Month.CurrentMonth -> "Current Month"
            is TemplatePeriodRule.Month.CustomMonth -> "${this.templatePeriodRule.monthDiff} months Ago"
        }
    }

    content[TemplateLabels.TimePeriod] = periodString
    content[TemplateLabels.ByType] = typeString

    return content
}

private fun Template.toOptionalContent(): Map<TemplateLabels, String> {
    val content = LinkedHashMap<TemplateLabels, String>()

    if (this.minAmount != null)
        content[TemplateLabels.MinAmount] = this.minAmount.toString()

    if (this.maxAmount != null)
        content[TemplateLabels.MaxAmount] = this.maxAmount.toString()

    if (this.includeKeywords.isNotEmpty())
        content[TemplateLabels.IncludedKeywords] = this.includeKeywords.joinToString(", ")

    if (this.excludeKeywords.isNotEmpty())
        content[TemplateLabels.ExcludedKeywords] = this.includeKeywords.joinToString(", ")

    if (this.selectedPlannedPayments.isNotNullOrEmpty())
        content[TemplateLabels.PlannedPayments] =
            this.selectedPlannedPayments.joinToString(separator = ",") {
                it.name.toLowerCaseLocal().capitalizeLocal()
            }

    if (this.transfersAsIncomeExpense)
        content[TemplateLabels.Others] = "Treat transfers as Income/Expense"

    return content
}