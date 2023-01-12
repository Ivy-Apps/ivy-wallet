package com.ivy.backup.base.data

import com.ivy.core.domain.action.transaction.transfer.TransferData

data class BatchTransferData(
    val batchId: String,
    val transfer: TransferData
)