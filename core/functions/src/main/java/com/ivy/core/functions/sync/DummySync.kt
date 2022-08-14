package com.ivy.core.functions.sync

import com.ivy.data.SyncMetadata

fun dummySync(
    isSynced: Boolean = false,
    isDeleted: Boolean = false,
): SyncMetadata = SyncMetadata(
    isSynced = isSynced,
    isDeleted = isDeleted
)