package com.ivy.sync.transaction

import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class SyncTrnAct @Inject constructor(
    private val transactionDao: TransactionDao,
    private val ivySession: IvySession,
) : FPAction<IOEffect<Transaction>, Unit>() {

    override suspend fun IOEffect<Transaction>.compose(): suspend () -> Unit = {
        sync(this)
    }

    private suspend fun sync(operation: IOEffect<Transaction>) {
        if (!ivySession.isLoggedIn()) return

        when (operation) {
            is IOEffect.Delete -> delete(operation.item)
            is IOEffect.Save -> save(operation.item)
        }
    }

    private suspend fun delete(item: Transaction) {
        // TODO: Delete HTTP Request

        transactionDao.deleteById(item.id)
    }

    private suspend fun save(item: Transaction) {
        //TODO: Sync HTTP request

        val syncedTrn = item.mark(
            isSynced = true,
            isDeleted = false
        )
        persist(syncedTrn)
    }

    private suspend fun persist(transaction: Transaction) {
        transactionDao.save(mapToEntity(transaction))
    }
}