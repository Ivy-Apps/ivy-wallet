package com.ivy.core.persistence.api.data

import com.ivy.core.data.sync.SyncState

data class Saveable<T>(
    val item: T,
    val sync: SyncState,
)