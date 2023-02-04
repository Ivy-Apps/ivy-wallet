package com.ivy.core.persistence.api.recurring

import com.ivy.core.data.RecurringRule
import com.ivy.core.data.RecurringRuleId
import com.ivy.core.data.TimeRange
import com.ivy.core.persistence.api.Read

interface RecurringRuleRead : Read<RecurringRule, RecurringRuleId, RecurringRuleRead.Query> {
    sealed interface Query {
        data class ForPeriod(val range: TimeRange) : Query
    }
}