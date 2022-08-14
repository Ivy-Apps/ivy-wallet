package com.ivy.wallet.domain.action.viewmodel.transaction

import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.io.persistence.data.toEntity
import javax.inject.Inject

class SaveTrnLocallyAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<TransactionOld, Unit>() {
    override suspend fun TransactionOld.compose(): suspend () -> Unit = {
        this.copy(
            isSynced = false
        ).toEntity()
    } then transactionDao::save
}