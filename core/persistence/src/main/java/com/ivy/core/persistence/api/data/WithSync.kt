package com.ivy.core.persistence.api.data

import com.ivy.core.data.sync.SyncState

@Deprecated("remove bad concept")
data class WithSync<T>(
    val item: T,
    val sync: SyncState,
)