package com.ivy.core.domain.action.transaction.transfer

import com.ivy.data.Value
import com.ivy.data.transaction.TrnTime

sealed interface ModifyTransfer {
    companion object {
        fun add(
            amountFrom: Value,
            amountTo: Value,
            accountFromId: String,
            accountToId: String,
            categoryId: String?,
            time: TrnTime,
            title: String?,
            description: String?
        ) {

        }
    }

    data class Add internal constructor(
        val dummy: String,
    ) : ModifyTransfer

    data class Delete internal constructor(val batchId: String) : ModifyTransfer
}