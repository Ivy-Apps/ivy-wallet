package com.ivy.sync.calculation

import com.ivy.core.data.sync.IvyWalletData
import com.ivy.core.data.sync.PartialIvyWalletData
import com.ivy.core.data.sync.SyncData
import com.ivy.core.data.sync.Syncable
import com.ivy.core.domain.calculation.syncDataFrom
import java.util.*

// TODO: Consider what happens when lastUpdated == lastUpdated? (check "onlyNewerThan" fun

fun merge(
    remote: IvyWalletData, // full remote backup JSON
    localPartial: PartialIvyWalletData // full DB but selecting only "id", "removed", "last_updated"
): MergeResult {
    val accounts = mergeItems(remote.accounts, localPartial.accounts)
    val transactions = mergeItems(remote.transactions, localPartial.transactions)
    val categories = mergeItems(remote.categories, localPartial.categories)
    val tags = mergeItems(remote.tags, localPartial.tags)
    val recurringRules = mergeItems(remote.recurringRules, localPartial.recurringRules)
    val attachments = mergeItems(remote.attachments, localPartial.attachments)
    val budgets = mergeItems(remote.budgets, localPartial.budgets)
    val savingGoals = mergeItems(remote.savingGoals, localPartial.savingGoals)
    val savingGoalRecords = mergeItems(remote.savingGoalRecords, localPartial.savingGoalRecords)

    // TODO: Filter transactions w/o account

    return MergeResult(
        localToUpdate = IvyWalletData(
            accounts = accounts.localToUpdate,
            transactions = transactions.localToUpdate,
            categories = categories.localToUpdate,
            tags = tags.localToUpdate,
            recurringRules = recurringRules.localToUpdate,
            attachments = attachments.localToUpdate,
            budgets = budgets.localToUpdate,
            savingGoals = savingGoals.localToUpdate,
            savingGoalRecords = savingGoalRecords.localToUpdate,
        ),
        remoteToUpdate = PartialIvyWalletData(
            accounts = accounts.remoteToUpdate,
            transactions = transactions.remoteToUpdate,
            categories = categories.remoteToUpdate,
            tags = tags.remoteToUpdate,
            recurringRules = recurringRules.remoteToUpdate,
            attachments = attachments.remoteToUpdate,
            budgets = budgets.remoteToUpdate,
            savingGoals = savingGoals.remoteToUpdate,
            savingGoalRecords = savingGoalRecords.remoteToUpdate
        )
    )
}

inline fun <reified T : Syncable> mergeItems(
    remote: SyncData<T>,
    local: SyncData<Syncable>
): MergeItem<T> {
    val remoteMap = buildMap(remote)
    val localMap = buildMap(local)

    return MergeItem(
        localToUpdate = syncDataFrom(remoteMap.onlyNewerThan(localMap)),
        remoteToUpdate = syncDataFrom(localMap.onlyNewerThan(remoteMap)),
    )
}

fun Map<UUID, Syncable>.onlyNewerThan(
    other: Map<UUID, Syncable>
): List<Syncable> {
    return values.filter {
        it.id !in other || // item is missing
                other[it.id] == null || // lastUpdate corrupted
                it.lastUpdated > other[it.id]?.lastUpdated // older version of the item
    }
}

fun <T : Syncable> buildMap(data: SyncData<T>): Map<UUID, Syncable> =
    data.items.associateBy(Syncable::id) +
            data.deleted.associateBy(Syncable::id)


data class MergeResult(
    val localToUpdate: IvyWalletData,
    val remoteToUpdate: PartialIvyWalletData
)

data class MergeItem<T : Syncable>(
    val localToUpdate: SyncData<T>,
    val remoteToUpdate: SyncData<Syncable>,
)