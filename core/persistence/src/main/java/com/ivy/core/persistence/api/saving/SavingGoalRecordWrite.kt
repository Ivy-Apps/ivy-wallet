package com.ivy.core.persistence.api.saving

import com.ivy.core.data.SavingGoalRecord
import com.ivy.core.data.SavingGoalRecordId
import com.ivy.core.persistence.api.Write

interface SavingGoalRecordWrite : Write<SavingGoalRecord, SavingGoalRecordId> {
}