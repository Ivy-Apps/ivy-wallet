package com.ivy.sync.calculation

import com.ivy.core.data.sync.IvyWalletData
import com.ivy.core.data.sync.SyncData
import com.ivy.core.data.sync.Syncable

fun updatedRemoteBackup(
    remote: IvyWalletData,
    newerLocal: IvyWalletData,
): IvyWalletData = IvyWalletData(
    accounts = updateRemote(remote.accounts, newerLocal.accounts),
    transactions = updateRemote(remote.transactions, newerLocal.transactions),
    categories = updateRemote(remote.categories, newerLocal.categories),
    tags = updateRemote(remote.tags, newerLocal.tags),
    recurringRules = updateRemote(remote.recurringRules, newerLocal.recurringRules),
    attachments = updateRemote(remote.attachments, newerLocal.attachments),
    budgets = updateRemote(remote.budgets, newerLocal.budgets),
    savingGoals = updateRemote(remote.savingGoals, newerLocal.savingGoals),
    savingGoalRecords = updateRemote(remote.savingGoalRecords, newerLocal.savingGoalRecords)
)

private fun <T : Syncable> updateRemote(
    remote: SyncData<T>,
    local: SyncData<T>
): SyncData<T> {
    val remoteItemsMap = remote.items.associateBy(Syncable::id).toMutableMap()

    // Overwrite remote with local
    local.items.forEach { localItem ->
        remoteItemsMap[localItem.id] = localItem
    }

    return SyncData(
        items = remoteItemsMap.values.toList(),
        deleted = remote.deleted + local.deleted
    )
}