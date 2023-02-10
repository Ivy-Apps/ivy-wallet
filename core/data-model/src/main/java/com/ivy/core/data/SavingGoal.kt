package com.ivy.core.data

import com.ivy.core.data.common.Archiveable
import com.ivy.core.data.common.ItemIconId
import com.ivy.core.data.common.Reorderable
import com.ivy.core.data.common.Value
import com.ivy.core.data.sync.Syncable
import com.ivy.core.data.sync.UniqueId
import java.time.LocalDateTime
import java.util.*

data class SavingGoal(
    override val id: SavingGoalId,
    val name: String,
    val description: String?,
    val url: String?,
    val iconId: ItemIconId,
    val amount: Value,
    val deadline: LocalDateTime,
    override val orderNum: Double,
    override val archived: Boolean,
    override val lastUpdated: LocalDateTime,
    override val removed: Boolean,
) : Reorderable, Archiveable, Syncable

@JvmInline
value class SavingGoalId(override val uuid: UUID) : UniqueId

data class SavingGoalRecord(
    override val id: SavingGoalRecordId,
    val savingGoalId: SavingGoalId,
    val accountId: AccountId,
    val type: SavingGoalRecordType,
    val amount: Value,
    val title: String?,
    val description: String?,
    override val lastUpdated: LocalDateTime,
    override val removed: Boolean,
) : Syncable

@JvmInline
value class SavingGoalRecordId(override val uuid: UUID) : UniqueId

enum class SavingGoalRecordType {
    Deposit, Withdraw
}