package com.ivy.core.persistence.api

import com.ivy.core.data.sync.Syncable
import com.ivy.core.data.sync.UniqueId

interface ReadSyncable<T, TID : UniqueId, Q> : Read<T, TID, Q> {
    suspend fun allPartial(): List<Syncable>

    suspend fun byIds(ids: List<TID>): List<T>
}