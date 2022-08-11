package com.ivy.sync.transaction

import com.ivy.data.SyncMetadata
import com.ivy.data.transaction.Transaction

fun Transaction.markSynced(
    isSynced: Boolean,
): Transaction = this.copy(
    metadata = this.metadata.copy(
        sync = SyncMetadata(
            isSynced = isSynced,
            isDeleted = false,
        )
    )
)

fun Transaction.markDeleted(
    isDeleted: Boolean,
): Transaction = this.copy(
    metadata = this.metadata.copy(
        sync = SyncMetadata(
            isSynced = false,
            isDeleted = isDeleted,
        )
    )
)