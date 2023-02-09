package com.ivy.core.persistence.api.recurring

import com.ivy.core.data.RecurringRule
import com.ivy.core.data.RecurringRuleId
import com.ivy.core.data.common.TimeRange
import com.ivy.core.persistence.api.ReadSyncable

interface RecurringRuleRead : ReadSyncable<RecurringRule, RecurringRuleId, RecurringRuleQuery> {

}

sealed interface RecurringRuleQuery {
    data class ForPeriod(val range: TimeRange) : RecurringRuleQuery
}