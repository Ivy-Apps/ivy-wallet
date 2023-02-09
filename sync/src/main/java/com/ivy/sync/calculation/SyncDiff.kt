package com.ivy.sync.calculation

import com.ivy.core.data.sync.IvyWalletData
import com.ivy.core.data.sync.PartialIvyWalletData
import com.ivy.core.data.sync.SyncData
import com.ivy.core.data.sync.Syncable
import com.ivy.core.domain.calculation.syncDataFrom
import java.util.*

/**
 * Calculates the diff that must be applied to sync [remote] and [localPartial].
 * This operation **prefers the [remote] version of history**.
 *
 * It has the following properties:
 * - **Idempotency**: it can be applied multiple times w/o changing the result.
 * - **Pure**: it has no side-effects.
 * - **Eventual Consistency**: will converge at some point on the same version of [IvyWalletData]
 * after apply multiple diffs on arbitrary [remote] and [localPartial] versions.
 *
 * @param remote a complete remote backup containing the entire Backup JSON for all items
 * @param localPartial a dump of the Local DB containing only [Syncable] info:
 * "id", "removed", and "last_updated
 * @return a complete [IvyWalletData] diff to apply to the Local DB and
 * an partial [IvyWalletData] ([Syncable] only) diff to apply to the Remote Backup
 */
fun calculateDiff(
    remote: IvyWalletData,
    localPartial: PartialIvyWalletData
): RemoteLocalDiff {
    val accounts = itemDiff(remote.accounts, localPartial.accounts)
    val transactions = itemDiff(remote.transactions, localPartial.transactions)
    val categories = itemDiff(remote.categories, localPartial.categories)
    val tags = itemDiff(remote.tags, localPartial.tags)
    val recurringRules = itemDiff(remote.recurringRules, localPartial.recurringRules)
    val attachments = itemDiff(remote.attachments, localPartial.attachments)
    val budgets = itemDiff(remote.budgets, localPartial.budgets)
    val savingGoals = itemDiff(remote.savingGoals, localPartial.savingGoals)
    val savingGoalRecords = itemDiff(remote.savingGoalRecords, localPartial.savingGoalRecords)

    return RemoteLocalDiff(
        local = IvyWalletData(
            accounts = accounts.local,
            transactions = transactions.local,
            categories = categories.local,
            tags = tags.local,
            recurringRules = recurringRules.local,
            attachments = attachments.local,
            budgets = budgets.local,
            savingGoals = savingGoals.local,
            savingGoalRecords = savingGoalRecords.local,
        ),
        remotePartial = PartialIvyWalletData(
            accounts = accounts.remotePartial,
            transactions = transactions.remotePartial,
            categories = categories.remotePartial,
            tags = tags.remotePartial,
            recurringRules = recurringRules.remotePartial,
            attachments = attachments.remotePartial,
            budgets = budgets.remotePartial,
            savingGoals = savingGoals.remotePartial,
            savingGoalRecords = savingGoalRecords.remotePartial
        )
    )
}

inline fun <reified T : Syncable> itemDiff(
    remote: SyncData<T>,
    local: SyncData<Syncable>
): RemoteLocalItemDiff<T> {
    val remoteMap = combineItemsAndDeleted(remote)
    val localMap = combineItemsAndDeleted(local)

    return RemoteLocalItemDiff(
        local = syncDataFrom(
            remoteMap.takeOnlyMissingOrNewer(
                localMap,
                // prefer remote on collision
                CollisionResolution.TakeLeft
            )
        ),
        remotePartial = syncDataFrom(
            localMap.takeOnlyMissingOrNewer(
                remoteMap,
                // prefer remote on collision
                CollisionResolution.TakeRight
            )
        ),
    )
}

fun Map<UUID, Syncable>.takeOnlyMissingOrNewer(
    right: Map<UUID, Syncable>,
    collision: CollisionResolution,
): List<Syncable> {
    return values.filter { leftItem ->
        val itemMissing = leftItem.id !in right
        if (itemMissing) return@filter true // take left

        val rightItem = right[leftItem.id] ?: error("impossible, must be in right!")
        when {
            leftItem.lastUpdated > rightItem.lastUpdated -> true // take left
            leftItem.lastUpdated < rightItem.lastUpdated -> false // take right
            else -> {
                // left.lastUpdated == rightItem.lastUpdated
                when (collision) {
                    CollisionResolution.TakeLeft -> true// take left
                    CollisionResolution.TakeRight -> false // take right
                }
            }
        }
    }
}

enum class CollisionResolution {
    TakeLeft, TakeRight
}

fun <T : Syncable> combineItemsAndDeleted(data: SyncData<T>): Map<UUID, Syncable> =
    data.items.associateBy(Syncable::id) +
            data.deleted.associateBy(Syncable::id)


data class RemoteLocalDiff(
    val local: IvyWalletData,
    val remotePartial: PartialIvyWalletData
)

data class RemoteLocalItemDiff<T : Syncable>(
    val local: SyncData<T>,
    val remotePartial: SyncData<Syncable>,
)