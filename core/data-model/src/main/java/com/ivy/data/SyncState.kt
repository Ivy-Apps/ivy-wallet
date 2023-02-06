package com.ivy.data

const val SYNCED = 1
const val SYNCING = 2
const val DELETING = 3

@Deprecated("will be removed!")
enum class SyncState(val code: Int) {
    Synced(SYNCED), Syncing(SYNCING), Deleting(DELETING);

    companion object {
        fun fromCode(code: Int): SyncState? = values().firstOrNull { it.code == code }
    }
}