package com.ivy.core.persistence.api.budget

import com.ivy.core.data.Budget
import com.ivy.core.data.BudgetId
import com.ivy.core.persistence.api.ReadSyncable

interface BudgetRead : ReadSyncable<Budget, BudgetId, BudgetQuery> {
}

sealed interface BudgetQuery
