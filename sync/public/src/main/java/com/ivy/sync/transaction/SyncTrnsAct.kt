package com.ivy.sync.transaction
/*

import com.ivy.data.SyncMetadata
import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.sync.base.SyncItem
import com.ivy.sync.ivyserver.transaction.TrnIvyServerSync
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject


class SyncTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao,
    private val ivyServerSync: TrnIvyServerSync,
) : FPAction<IOEffect<List<Transaction>>, Unit>() {

    override suspend fun IOEffect<List<Transaction>>.compose(): suspend () -> Unit = {
        sync(this)
    }

    private suspend fun sync(operation: IOEffect<List<Transaction>>) {
        val sync = ivyServerSync.enabled() ?: return

        when (operation) {
            is IOEffect.Delete -> delete(sync, operation.item)
            is IOEffect.Save -> save(sync, operation.item)
        }
    }

    private suspend fun delete(sync: SyncItem<Transaction>, items: List<Transaction>) {
        sync.delete(items)
        // delete all locally not matter the result
        items.forEach { transactionDao.deleteById(it.id) }
    }

    private suspend fun save(sync: SyncItem<Transaction>, items: List<Transaction>) =
        sync.save(items)
            .map {
                val syncedItem = it.mark(
                    isSynced = true,
                    isDeleted = false
                )
                persist(syncedItem)
            }


    private suspend fun persist(item: Transaction) {
        transactionDao.save(mapToEntity(item))
    }
}*/
