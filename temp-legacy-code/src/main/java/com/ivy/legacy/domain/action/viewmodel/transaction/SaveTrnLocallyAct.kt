package com.ivy.wallet.domain.action.viewmodel.transaction

import com.ivy.base.legacy.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.legacy.datamodel.toEntity
import com.ivy.data.db.dao.write.WriteTransactionDao
import javax.inject.Inject

class SaveTrnLocallyAct @Inject constructor(
    private val writeTransactionDao: WriteTransactionDao,
) : FPAction<Transaction, Unit>() {
    override suspend fun Transaction.compose(): suspend () -> Unit = {
        this.copy(
            isSynced = false
        ).toEntity()
    } then writeTransactionDao::save then {}
}
