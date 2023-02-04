package com.ivy.core.persistence.api

import kotlinx.coroutines.flow.Flow

interface Read<T, TID, Q> {
    fun single(id: TID): Flow<T?>

    fun many(query: Q): Flow<List<T>>
}