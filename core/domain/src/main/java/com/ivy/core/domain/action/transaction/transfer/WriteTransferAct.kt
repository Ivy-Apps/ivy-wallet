package com.ivy.core.domain.action.transaction.transfer

import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.action.transaction.WriteTrnsBatchAct
import com.ivy.core.domain.pure.transaction.transfer.validateTransfer
import com.ivy.data.Sync
import com.ivy.data.Value
import com.ivy.data.transaction.*
import java.util.*
import javax.inject.Inject

class WriteTransferAct @Inject constructor(
    private val writeTrnsBatchAct: WriteTrnsBatchAct,
    private val transferByBatchIdAct: TransferByBatchIdAct,
    private val writeTrnsAct: WriteTrnsAct,
) : Action<ModifyTransfer, Unit>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(modify: ModifyTransfer) {
        when (modify) {
            is ModifyTransfer.Add -> addTransfer(modify.batchId, modify.data)
            is ModifyTransfer.Edit -> editTransfer(modify.batchId, modify.data)
            is ModifyTransfer.Delete -> deleteTransfer(modify.transfer)
            is ModifyTransfer.UpdateTrnTime -> updateTrnTime(modify.batchId, modify.newTrnTime)
        }
    }

    private suspend fun updateTrnTime(batchId: String, newTime: TrnTime) {
        val transfer = transferByBatchIdAct(batchId) ?: return
        listOfNotNull(transfer.from, transfer.to, transfer.fee).forEach { trn ->
            val actualTrn = trn.copy(time = newTime)
            writeTrnsAct(
                WriteTrnsAct.Input.Update(
                    old = trn,
                    new = actualTrn
                )
            )
        }
    }

    private suspend fun addTransfer(
        batchId: String?,
        data: TransferData
    ) {
        if (!validateTransfer(data)) return

        val trns = mutableListOf<Transaction>()
        val metadata = TrnMetadata(
            recurringRuleId = null,
            loanId = null,
            loanRecordId = null,
        )

        // FROM
        trns.add(
            Transaction(
                id = UUID.randomUUID(),
                account = data.accountFrom,
                value = data.amountFrom,
                category = data.category,
                title = data.title,
                description = data.description,
                time = data.time,
                type = TransactionType.Expense,
                purpose = TrnPurpose.TransferFrom,
                metadata = metadata,
                attachments = emptyList(),
                tags = emptyList(),
                state = TrnState.Default,
                sync = data.sync,
            )
        )

        // TO
        trns.add(
            Transaction(
                id = UUID.randomUUID(),
                account = data.accountTo,
                value = data.amountTo,
                category = data.category,
                title = data.title,
                description = data.description,
                time = data.time,
                type = TransactionType.Income,
                purpose = TrnPurpose.TransferTo,
                metadata = metadata,
                attachments = emptyList(),
                tags = emptyList(),
                state = TrnState.Default,
                sync = data.sync,
            )
        )

        // FEE
        if (data.fee != null) {
            trns.add(
                fee(
                    data = data,
                    fee = data.fee,
                    metadata = metadata,
                    sync = data.sync,
                )
            )
        }

        writeTrnsBatchAct(
            WriteTrnsBatchAct.save(
                TrnBatch(
                    batchId = batchId ?: UUID.randomUUID().toString(),
                    trns = trns
                )
            )
        )
    }

    private suspend fun editTransfer(
        batchId: String,
        data: TransferData
    ) {
        if (!validateTransfer(data)) return
        val transfer = transferByBatchIdAct(batchId) ?: return

        val trns = mutableListOf<Transaction>()

        // FROM
        trns.add(
            transfer.from.copy(
                account = data.accountFrom,
                value = data.amountFrom,
                category = data.category,
                title = data.title,
                description = data.description,
                time = data.time,
                sync = data.sync,
            )
        )

        // TO
        trns.add(
            transfer.to.copy(
                account = data.accountTo,
                value = data.amountTo,
                category = data.category,
                title = data.title,
                description = data.description,
                time = data.time,
                sync = data.sync,
            )
        )

        // FEE
        if (data.fee != null) {
            // will have fee
            val existingFee = transfer.fee
            if (existingFee != null) {
                // update existing fee
                trns.add(
                    existingFee.copy(
                        account = data.accountFrom,
                        category = data.category,
                        title = data.title,
                        description = data.description,
                        time = data.time,
                        value = data.fee,
                        sync = data.sync,
                    )
                )
            } else {
                // add new fee
                trns.add(
                    fee(
                        data = data,
                        fee = data.fee,
                        sync = data.sync,
                    )
                )
            }
        } else {
            // will have NO fee
            transfer.fee?.let { fee ->
                // remove existing fee if any
                writeTrnsAct(
                    WriteTrnsAct.Input.Delete(
                        trnId = fee.id.toString(),
                        affectedAccountIds = setOf(fee.account.id.toString()),
                        originalTime = fee.time
                    )
                )
            }
        }

        writeTrnsBatchAct(
            WriteTrnsBatchAct.save(
                TrnBatch(
                    batchId = batchId,
                    trns = trns
                )
            )
        )
    }

    private fun fee(
        data: TransferData,
        fee: Value,
        sync: Sync,
        metadata: TrnMetadata = TrnMetadata(
            recurringRuleId = null,
            loanId = null,
            loanRecordId = null,
        )
    ): Transaction = Transaction(
        id = UUID.randomUUID(),
        account = data.accountFrom,
        value = fee,
        category = data.category,
        title = data.title,
        description = data.description,
        time = data.time,
        type = TransactionType.Expense,
        purpose = TrnPurpose.Fee,
        metadata = metadata,
        attachments = emptyList(),
        tags = emptyList(),
        state = TrnState.Default,
        sync = sync,
    )

    private suspend fun deleteTransfer(
        transfer: Transfer
    ) {
        writeTrnsBatchAct(
            WriteTrnsBatchAct.delete(
                TrnBatch(
                    batchId = transfer.batchId,
                    trns = listOfNotNull(transfer.from, transfer.to, transfer.fee)
                )
            )
        )
    }
}
