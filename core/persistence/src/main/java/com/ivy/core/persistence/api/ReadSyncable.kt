package com.ivy.core.persistence.api

import com.ivy.core.data.sync.Syncable

interface ReadSyncable<T, TID, Q> : Read<T, TID, Q> {
    suspend fun all(): List<Syncable>
}