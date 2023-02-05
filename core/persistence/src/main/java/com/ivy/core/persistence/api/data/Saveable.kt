package com.ivy.core.persistence.api.data

data class Saveable<T>(
    val item: T,
    val sync: SyncState,
)