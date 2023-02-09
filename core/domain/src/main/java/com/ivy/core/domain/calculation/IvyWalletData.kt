package com.ivy.core.domain.calculation

import com.ivy.core.data.sync.SyncData
import com.ivy.core.data.sync.Syncable

// TODO: Fix this, it's not type-safe!
/**
 * @param syncables combined [Syncable.removed] and not-removed syncables
 */
inline fun <reified T : Syncable> syncDataFrom(syncables: List<Syncable>): SyncData<T> {
    val map = syncables.groupBy { it.removed }
    return SyncData(
        items = map[false]?.map { it as T } ?: emptyList(), // not deleted
        deleted = map[true]?.toSet() ?: emptySet()
    )
}