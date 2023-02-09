package com.ivy.sync.calculation

import com.ivy.core.data.sync.IvyWalletData
import com.ivy.core.data.sync.SyncData
import com.ivy.core.data.sync.Syncable

internal fun IvyWalletData.applyDiff(
    diff: IvyWalletData,
): IvyWalletData = IvyWalletData(
    accounts = accounts.applyDiff(diff.accounts),
    transactions = transactions.applyDiff(diff.transactions),
    categories = categories.applyDiff(diff.categories),
    tags = tags.applyDiff(diff.tags),
    recurringRules = recurringRules.applyDiff(diff.recurringRules),
    attachments = attachments.applyDiff(diff.attachments),
    budgets = budgets.applyDiff(diff.budgets),
    savingGoals = savingGoals.applyDiff(diff.savingGoals),
    savingGoalRecords = savingGoalRecords.applyDiff(diff.savingGoalRecords)
)

private fun <T : Syncable> SyncData<T>.applyDiff(
    diff: SyncData<T>
): SyncData<T> {
    val sourceItems = items.associateBy(Syncable::id).toMutableMap()

    // Overwrite remote with local
    diff.items.forEach { localItem ->
        sourceItems[localItem.id] = localItem
    }

    return SyncData(
        items = sourceItems.values.toList(),
        // The "deleted" set is just inflated
        // TODO: We may introduce an operation to clear "tombstones" which are grow-only
        deleted = deleted + diff.deleted
    )
}