package com.ivy.core.domain.action.transaction.transfer

import com.ivy.data.transaction.TrnListItem

sealed interface ModifyTransfer {
    companion object {
        fun add(data: TransferData, batchId: String? = null) = Add(data = data, batchId = batchId)

        fun edit(batchId: String, data: TransferData) = Edit(batchId, data)

        fun dueToActual(batchId: String) = DueToActual(batchId)

        fun delete(transfer: TrnListItem.Transfer) = Delete(transfer)
    }

    data class DueToActual internal constructor(val batchId: String) : ModifyTransfer

    data class Add internal constructor(
        val batchId: String?,
        val data: TransferData
    ) : ModifyTransfer

    data class Edit internal constructor(
        val batchId: String,
        val data: TransferData
    ) : ModifyTransfer

    data class Delete internal constructor(val transfer: TrnListItem.Transfer) : ModifyTransfer
}