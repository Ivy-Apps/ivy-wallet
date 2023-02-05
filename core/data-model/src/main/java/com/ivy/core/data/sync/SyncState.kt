package com.ivy.core.data.sync

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption

const val StateSynced = 1
const val StateSyncing = 2
const val StateDeleting = 3

enum class SyncState(val code: Int) {
    Synced(StateSynced), Syncing(StateSyncing), Deleting(StateDeleting);

    companion object {
        fun fromIntUnsafe(code: Int): SyncState = fromInt(code).getOrElse {
            error("SyncFlag error: invalid code - $code.")
        }

        fun fromInt(code: Int): Option<SyncState> =
            SyncState.values().firstOrNull { it.code == code }.toOption()
    }
}