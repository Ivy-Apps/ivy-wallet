package com.ivy.core.action.transaction.write

import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.sync.SyncTask
import com.ivy.sync.syncTaskFrom
import com.ivy.sync.transaction.SyncTrnAct
import com.ivy.temp.persistence.Operation
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class WriteTrnAct @Inject constructor(
    private val transactionDao: TransactionDao,
    private val syncTrnAct: SyncTrnAct
) : FPAction<Operation<Transaction>, SyncTask>() {

    override suspend fun Operation<Transaction>.compose(): suspend () -> SyncTask = {
        when (this) {
            is Operation.Save -> {
                transactionDao.save(mapToEntity(item))
            }
            is Operation.Delete -> TODO()
        }

        syncTaskFrom(this asParamTo syncTrnAct)
    }
}