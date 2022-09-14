package com.ivy.data

enum class SyncState(val code: Int) {
    Synced(1), Syncing(2), Deleting(3);

    companion object {
        fun fromCode(code: Int): SyncState? = values().firstOrNull { it.code == code }
    }
}