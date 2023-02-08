package com.ivy.core.domain.api.action.write

import arrow.core.NonEmptyList
import com.ivy.core.data.Transaction
import com.ivy.core.data.TransactionId

class WriteTransactionAct {
    // TODO: Implement
}

sealed interface ModifyTransaction {
    data class Save(
        val transaction: Transaction,
    ) : ModifyTransaction

    data class SaveMany(
        val transactions: NonEmptyList<Transaction>
    ) : ModifyTransaction

    data class Delete(
        val id: TransactionId,
    ) : ModifyTransaction

    data class DeleteMany(
        val ids: List<TransactionId>
    ) : ModifyTransaction
}