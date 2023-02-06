package com.ivy.backup.base.data

import com.ivy.core.domain.action.transaction.transfer.TransferData

@Deprecated("will be removed!")
data class BatchTransferData(
    val batchId: String,
    val transfer: TransferData
)