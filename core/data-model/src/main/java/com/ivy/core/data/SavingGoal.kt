package com.ivy.core.data

import com.ivy.core.data.common.*
import java.time.LocalDateTime
import java.util.*

data class SavingGoal(
    val id: UUID,
    val name: String,
    val description: String?,
    val url: String?,
    val iconId: ItemIconId,
    val amount: Value,
    val deadline: LocalDateTime,
    override val orderNum: Double,
    override val archived: Boolean,
    override val lastUpdated: LocalDateTime,
) : Reorderable, Archiveable, Syncable

@JvmInline
value class SavingGoalId(val id: UUID)

data class SavingGoalRecord(
    val id: UUID,
    val savingGoalId: SavingGoalId,
    val accountId: AccountId,
    val type: SavingGoalRecordType,
    val amount: Value,
    val title: String?,
    val description: String?,
)

enum class SavingGoalRecordType {
    Deposit, Withdraw
}