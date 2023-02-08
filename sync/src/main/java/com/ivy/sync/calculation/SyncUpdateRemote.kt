package com.ivy.sync.calculation

import com.ivy.core.data.sync.IvyWalletData
import com.ivy.core.data.sync.SyncData
import com.ivy.core.data.sync.Syncable

fun updatedRemoteBackup(
    remote: IvyWalletData,
    newerLocal: IvyWalletData,
): IvyWalletData = IvyWalletData(
    accounts = applyNewerLocal(remote.accounts, newerLocal.accounts),
    transactions = applyNewerLocal(remote.transactions, newerLocal.transactions),
    categories = applyNewerLocal(remote.categories, newerLocal.categories),
    tags = applyNewerLocal(remote.tags, newerLocal.tags),
    recurringRules = applyNewerLocal(remote.recurringRules, newerLocal.recurringRules),
    attachments = applyNewerLocal(remote.attachments, newerLocal.attachments),
    budgets = applyNewerLocal(remote.budgets, newerLocal.budgets),
    savingGoals = applyNewerLocal(remote.savingGoals, newerLocal.savingGoals),
)

private fun <T : Syncable> applyNewerLocal(
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