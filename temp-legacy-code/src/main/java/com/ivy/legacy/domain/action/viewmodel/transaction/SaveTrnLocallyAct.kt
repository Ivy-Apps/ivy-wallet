package com.ivy.wallet.domain.action.viewmodel.transaction

import com.ivy.core.data.db.write.TransactionWriter
import com.ivy.core.datamodel.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import javax.inject.Inject

class SaveTrnLocallyAct @Inject constructor(
    private val transactionWriter: TransactionWriter,
) : FPAction<Transaction, Unit>() {
    override suspend fun Transaction.compose(): suspend () -> Unit = {
        this.copy(
            isSynced = false
        ).toEntity()
    } then transactionWriter::save then {}
}
