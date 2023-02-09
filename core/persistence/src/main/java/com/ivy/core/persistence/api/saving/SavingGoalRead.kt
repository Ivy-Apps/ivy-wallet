package com.ivy.core.persistence.api.saving

import com.ivy.core.data.SavingGoal
import com.ivy.core.data.SavingGoalId
import com.ivy.core.persistence.api.ReadSyncable

interface SavingGoalRead : ReadSyncable<SavingGoal, SavingGoalId, SavingGoalQuery> {
}

sealed interface SavingGoalQuery
