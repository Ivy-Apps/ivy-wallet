package com.ivy.core.domain.action.transaction.transfer

sealed interface ModifyTransfer {
    companion object {
        fun add(data: TransferData) = Add(data)

        fun edit(batchId: String, data: TransferData) = Edit(batchId, data)

        fun delete(batchId: String) = Delete(batchId)
    }

    data class Add internal constructor(
        val data: TransferData
    ) : ModifyTransfer

    data class Edit internal constructor(
        val batchId: String,
        val data: TransferData
    ) : ModifyTransfer

    data class Delete internal constructor(val batchId: String) : ModifyTransfer
}