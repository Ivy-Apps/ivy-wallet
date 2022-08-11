package com.ivy.sync.transaction

import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.temp.persistence.Operation
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class SyncTrnAct @Inject constructor(
    private val transactionDao: TransactionDao,
    private val ivySession: IvySession,
) : FPAction<Operation<Transaction>, Unit>() {

    override suspend fun Operation<Transaction>.compose(): suspend () -> Unit = {
        sync(this)
    }

    private suspend fun sync(operation: Operation<Transaction>) {
        if (!ivySession.isLoggedIn()) return

        with(operation) {
            when (operation) {
                is Operation.Delete -> TODO()
                is Operation.Save -> {
                    saveSync()
                }
            }
        }
    }

    private suspend fun Operation<Transaction>.saveSync() {
        val notSyncedTrn = item.markSynced(
            isSynced = false
        )
        transactionDao.save(mapToEntity(notSyncedTrn))

        //TODO: Sync HTTP request

        val syncedTrn = notSyncedTrn.markSynced(
            isSynced = true
        )
        transactionDao.save(mapToEntity(syncedTrn))
    }
}