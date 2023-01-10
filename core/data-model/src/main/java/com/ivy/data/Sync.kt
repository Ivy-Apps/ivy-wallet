package com.ivy.data

import java.time.LocalDateTime

data class Sync(
    val state: SyncState,
    val lastUpdated: LocalDateTime,
)