package com.ivy.sync.transaction

import com.ivy.data.SyncMetadata
import com.ivy.data.transaction.Transaction

fun Transaction.mark(
    isSynced: Boolean,
    isDeleted: Boolean
): Transaction = this.copy(
    metadata = this.metadata.copy(
        sync = SyncMetadata(
            isSynced = isSynced,
            isDeleted = isDeleted,
        )
    )
)