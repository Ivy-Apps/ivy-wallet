package com.ivy.sync

interface SyncTask {
    suspend fun sync()
}

fun syncTaskFrom(
    f: suspend () -> Unit
): SyncTask = object : SyncTask {
    override suspend fun sync() {
        f()
    }

}