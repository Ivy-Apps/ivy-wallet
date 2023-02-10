package com.ivy.core.persistence.api

import com.ivy.core.data.sync.UniqueId
import kotlinx.coroutines.flow.Flow

interface Read<T, TID : UniqueId, Q> {
    fun single(id: TID): Flow<T?>

    fun many(query: Q): Flow<List<T>>
}