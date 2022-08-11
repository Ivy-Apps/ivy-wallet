package com.ivy.core.action.transaction.write

import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class WriteTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<List<Transaction>, Unit>() {
    override suspend fun List<Transaction>.compose(): suspend () -> Unit = {
        //TODO: Handle sync
        transactionDao.save(this.map(::mapToEntity))
    }
}