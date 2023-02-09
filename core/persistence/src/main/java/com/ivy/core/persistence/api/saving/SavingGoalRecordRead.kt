package com.ivy.core.persistence.api.saving

import com.ivy.core.data.SavingGoalRecord
import com.ivy.core.data.SavingGoalRecordId
import com.ivy.core.persistence.api.ReadSyncable

interface SavingGoalRecordRead :
    ReadSyncable<SavingGoalRecord, SavingGoalRecordId, SavingGoalRecordQuery> {
}

sealed interface SavingGoalRecordQuery
