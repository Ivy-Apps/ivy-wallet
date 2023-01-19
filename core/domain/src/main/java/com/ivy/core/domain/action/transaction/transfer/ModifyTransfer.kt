package com.ivy.core.domain.action.transaction.transfer

import com.ivy.data.transaction.Transfer
import com.ivy.data.transaction.TrnTime

sealed interface ModifyTransfer {
    companion object {
        fun add(data: TransferData, batchId: String? = null) = Add(data = data, batchId = batchId)

        fun edit(batchId: String, data: TransferData) = Edit(batchId, data)

        fun updateTrnTime(batchId: String, newTrnTime: TrnTime) =
            UpdateTrnTime(batchId, newTrnTime)

        fun delete(transfer: Transfer) = Delete(transfer)
    }

    data class UpdateTrnTime internal constructor(
        val batchId: String,
        val newTrnTime: TrnTime,
    ) : ModifyTransfer

    data class Add internal constructor(
        val batchId: String?,
        val data: TransferData
    ) : ModifyTransfer

    data class Edit internal constructor(
        val batchId: String,
        val data: TransferData
    ) : ModifyTransfer

    data class Delete internal constructor(val transfer: Transfer) : ModifyTransfer
}