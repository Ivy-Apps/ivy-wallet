package com.ivy.backup.base

import com.ivy.backup.base.data.BackupData
import com.ivy.backup.base.data.BatchTransferData
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.account.WriteAccountsAct
import com.ivy.core.domain.action.category.WriteCategoriesAct
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.action.settings.basecurrency.WriteBaseCurrencyAct
import com.ivy.core.domain.action.settings.theme.WriteThemeAct
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.action.transaction.transfer.ModifyTransfer
import com.ivy.core.domain.action.transaction.transfer.TransferByBatchIdAct
import com.ivy.core.domain.action.transaction.transfer.WriteTransferAct
import javax.inject.Inject

class WriteBackupDataAct @Inject constructor(
    private val writeAccountsAct: WriteAccountsAct,
    private val writeCategoriesAct: WriteCategoriesAct,
    private val writeTrnsAct: WriteTrnsAct,
    private val writeTransferAct: WriteTransferAct,
    private val writeBaseCurrencyAct: WriteBaseCurrencyAct,
    private val writeThemeAct: WriteThemeAct,
    private val transferByBatchIdAct: TransferByBatchIdAct
) : Action<BackupData, Unit>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(backup: BackupData) {
        // region restore Settings
        writeBaseCurrencyAct(backup.settings.baseCurrency)
        writeThemeAct(backup.settings.theme)
        // endregion

        writeAccountsAct(Modify.saveMany(backup.accounts))
        writeCategoriesAct(Modify.saveMany(backup.categories))
        writeTrnsAct(Modify.saveMany(backup.transactions))

        restoreTransfers(backup.transfers.items)
    }

    private suspend fun restoreTransfers(
        transfersData: List<BatchTransferData>
    ) {
        for (data in transfersData) {
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
                    ModifyTransfer.add(data.transfer)
                )
            }
        }
    }
}