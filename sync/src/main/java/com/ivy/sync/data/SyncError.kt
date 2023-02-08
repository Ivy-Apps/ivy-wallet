package com.ivy.sync.data

sealed interface SyncError {
    val reason: Throwable

    data class Generic(override val reason: Throwable) : SyncError
}