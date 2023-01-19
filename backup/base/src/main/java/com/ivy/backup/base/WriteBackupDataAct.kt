package com.ivy.backup.base

import com.ivy.backup.base.data.BackupData
import com.ivy.backup.base.data.BatchTransferData
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.account.WriteAccountsAct
import com.ivy.core.domain.action.category.WriteCategoriesAct
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.action.settings.basecurrency.WriteBaseCurrencyAct
import com.ivy.core.domain.action.settings.theme.WriteThemeAct
import com.ivy.core.domain.action.transaction.TrnsSignal
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.action.transaction.transfer.ModifyTransfer
import com.ivy.core.domain.action.transaction.transfer.TransferByBatchIdAct
import com.ivy.core.domain.action.transaction.transfer.WriteTransferAct
import com.ivy.data.transaction.Transaction
import javax.inject.Inject

class WriteBackupDataAct @Inject constructor(
    private val writeAccountsAct: WriteAccountsAct,
    private val writeCategoriesAct: WriteCategoriesAct,
    private val writeTrnsAct: WriteTrnsAct,
    private val writeTransferAct: WriteTransferAct,
    private val writeBaseCurrencyAct: WriteBaseCurrencyAct,
    private val writeThemeAct: WriteThemeAct,
    private val transferByBatchIdAct: TransferByBatchIdAct,
    private val trnsSignal: TrnsSignal,
) : Action<WriteBackupDataAct.Input, Unit>() {
    data class Input(
        val backup: BackupData,
        val onProgress: OnImportProgress?,
    )

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(input: Input) {
        val backup = input.backup
        val progress = { progress: Float, message: String ->
            input.onProgress?.onProgress(progress, message)
        }

        progress(0f, "Savings to database started...")
        // region restore Settings
        writeBaseCurrencyAct(backup.settings.baseCurrency)
        writeThemeAct(backup.settings.theme)
        // endregion
        progress(0.05f, "Settings and theme imported.")

        writeAccountsAct(Modify.saveMany(backup.accounts))
        progress(0.1f, "[ACCOUNTS] ${backup.accounts.size} accounts imported.")
        writeCategoriesAct(Modify.saveMany(backup.categories))
        progress(0.15f, "[CATEGORIES] ${backup.categories.size} categories imported.")

        progress(0.2f, "[TRANSACTIONS] Importing transactions...")
        // TODO: Remove trnsSingal later cuz TrnsFlow is deprecated
        trnsSignal.disable() // prevent spam
        writeTrnsPaginated(
            trns = backup.transactions,
            pageSize = 100,
            importedTrns = 0,
            totalTrns = backup.transactions.size,
            progress = { percent, message ->
                // Transactions take from 20% to 75%
                val adjustedPercent = 0.2f + (0.55f * percent)
                progress(adjustedPercent, message)
            },
        )

        progress(0.76f, "[TRANSFERS] Importing transfers...")
        restoreTransfers(
            transfersData = backup.transfers.items,
            progress = { percent, message ->
                val adjustedPercent = 0.76f + (0.24f * percent)
                progress(adjustedPercent, message)
            }
        )
        trnsSignal.enable()
        progress(1f, "[WRITE SUCCESSFUL] Import completed!")
    }

    private tailrec suspend fun writeTrnsPaginated(
        trns: List<Transaction>,
        pageSize: Int,
        importedTrns: Int,
        totalTrns: Int,
        progress: (Float, String) -> Unit,
    ) {
        if (trns.isNotEmpty()) {
            writeTrnsAct(
                WriteTrnsAct.Input.ManyInefficient(
                    trns.take(pageSize).map {
                        WriteTrnsAct.Input.SaveInefficient(it)
                    }
                )
            )

            progress(
                importedTrns.toFloat() / totalTrns,
                "[TRANSACTIONS] Imported $importedTrns/$totalTrns transactions."
            )
            writeTrnsPaginated(
                trns = trns.drop(pageSize),
                pageSize = pageSize,
                importedTrns = importedTrns + pageSize,
                totalTrns = totalTrns,
                progress = progress,
            )
        }
    }

    private suspend fun restoreTransfers(
        transfersData: List<BatchTransferData>,
        progress: (Float, String) -> Unit
    ) {
        val total = transfersData.size
        for ((index, data) in transfersData.withIndex()) {
            if (transferByBatchIdAct(data.batchId) != null) {
                // transfer already exists, update it
                writeTransferAct(
                    ModifyTransfer.edit(
                        batchId = data.batchId,
                        data = data.transfer
                    )
                )
            } else {
                // it's a new transfer, add it
                writeTransferAct(
                    ModifyTransfer.add(
                        batchId = data.batchId,
                        data = data.transfer
                    )
                )
            }
            progress(
                (index + 1) / total.toFloat(),
                "[TRANSFERS] Imported ${index + 1}/$total transfers"
            )
        }
    }
}