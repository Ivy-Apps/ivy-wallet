package com.ivy.reports.template.functions

import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.reports.data.ReportFilterState
import com.ivy.reports.template.data.Template
import com.ivy.reports.template.data.TemplatePeriodRule
import com.ivy.wallet.utils.timeNowLocal
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

fun ReportFilterState.toTemplate(templateName: String = ""): Template? {
    val timePeriod = this.period
    timePeriod ?: return null

    val periodRule = timePeriod.toPeriodRule()

    with(this) {
        return Template(
            title = templateName,
            selectedTrnTypes = selectedTrnTypes,
            templatePeriodRule = periodRule,
            selectedAccounts = selectedAccounts.filter { it.selected }.map { it.account },
            selectedCategories = selectedCategories.filter { it.selected }
                .map { it.selectableCategory },
            minAmount = minAmount,
            maxAmount = maxAmount,
            includeKeywords = includeKeywords,
            excludeKeywords = excludeKeywords,
            selectedPlannedPayments = selectedPlannedPayments,
            transfersAsIncomeExpense = transfersAsIncomeExpense
        )
    }
}

private fun TimePeriod.toPeriodRule(): TemplatePeriodRule {
    val month = this.month
    val year = this.year
    val lastN = this.lastNRange
    val fromToRange = this.fromToRange

    val from = fromToRange?.from
    val to = fromToRange?.to

    val today = timeNowLocal().toLocalDate()
    val currentMonthValue = today.month.value
    val currentYear = today.year

    if (month != null && year != null) {
        if (month.monthValue == currentMonthValue && year == currentYear)
            return TemplatePeriodRule.Month.CurrentMonth

        val selectedDate = LocalDate.now().withYear(year).withMonth(month.monthValue)
        val monthDiff = ChronoUnit.MONTHS.between(
            YearMonth.from(selectedDate),
            YearMonth.from(today)
        ).toInt()

        if (monthDiff >= 0)
            return TemplatePeriodRule.Month.CustomMonth(monthDiff = monthDiff)

    }

    if (from == null && to != null && to.toLocalDate().isEqual(today))
        return TemplatePeriodRule.AllTime

    if (lastN != null)
        return TemplatePeriodRule.LastN(lastN = lastN.periodN, intervalType = lastN.periodType)


    return TemplatePeriodRule.Custom
}