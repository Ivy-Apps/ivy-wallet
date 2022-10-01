package com.ivy.reports.template.data

import com.ivy.data.planned.IntervalType

sealed class TemplatePeriodRule {
    object AllTime : TemplatePeriodRule()
    object Custom : TemplatePeriodRule()

    sealed class Month : TemplatePeriodRule() {
        object CurrentMonth : Month()
        data class CustomMonth(val monthDiff: Int) : Month()
    }

    data class LastN(val lastN: Int, val intervalType: IntervalType) : TemplatePeriodRule()
}