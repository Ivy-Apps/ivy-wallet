package com.ivy.core.persistence.api.data

import java.time.LocalDateTime

data class Saveable<T>(
    val item: T,
    val sync: SyncState,
    val lastUpdated: LocalDateTime,
)