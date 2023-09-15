package com.ivy.wallet.domain.action.viewmodel.transaction

import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.core.data.model.Transaction
import com.ivy.core.data.db.dao.TransactionDao
import javax.inject.Inject

class SaveTrnLocallyAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<Transaction, Unit>() {
    override suspend fun Transaction.compose(): suspend () -> Unit = {
        this.copy(
            isSynced = false
        ).toEntity()
    } then transactionDao::save
}
