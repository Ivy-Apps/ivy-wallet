package com.ivy.core.domain.calculation.recurring

import com.ivy.core.data.RecurringRule
import com.ivy.core.data.Transaction
import com.ivy.core.data.common.TimeRange


fun generateRecurring(
    rule: RecurringRule,
    ruleExceptions: List<Transaction>,
    period: TimeRange,
): List<Transaction> = TODO()