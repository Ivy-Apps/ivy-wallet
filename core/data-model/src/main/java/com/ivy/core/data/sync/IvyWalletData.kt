package com.ivy.core.data.sync

import com.ivy.core.data.*
import java.time.LocalDateTime
import java.util.*


data class PartialIvyWalletData(
    val accounts: SyncData<Syncable>,
    val transactions: SyncData<Syncable>,
    val categories: SyncData<Syncable>,
    val tags: SyncData<Syncable>,
    val recurringRules: SyncData<Syncable>,
    val attachments: SyncData<Syncable>,
    val budgets: SyncData<Syncable>,
    val savingGoals: SyncData<Syncable>,
)

data class IvyWalletData(
    val accounts: SyncData<Account>,
    val transactions: SyncData<Transaction>,
    val categories: SyncData<Category>,
    val tags: SyncData<Tag>,
    val recurringRules: SyncData<RecurringRule>,
    val attachments: SyncData<Attachment>,
    val budgets: SyncData<Budget>,
    val savingGoals: SyncData<SavingGoal>,
)

data class SyncData<out T : Syncable>(
    val items: List<T>,
    val deleted: Set<Syncable>
)

// TODO: This doesn't seem perfect because "removed" is duplicated.
data class DeletionRecord(
    override val id: UUID,
    override val lastUpdated: LocalDateTime,
    override val removed: Boolean,
) : Syncable
