package com.ivy.core.action.transaction

import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.sync.SyncTask
import com.ivy.sync.syncTaskFrom
import com.ivy.sync.transaction.SyncTrnsAct
import com.ivy.sync.transaction.mark
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class WriteTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao,
    private val syncTrnsAct: SyncTrnsAct
) : FPAction<IOEffect<List<Transaction>>, SyncTask>() {
    override suspend fun IOEffect<List<Transaction>>.compose(): suspend () -> SyncTask = {
        when (this) {
            is IOEffect.Save -> save(trns = item)
            is IOEffect.Delete -> delete(trns = item)
        }

        syncTaskFrom(this asParamTo syncTrnsAct)
    }

    private suspend fun save(trns: List<Transaction>) = persist(
        trns = trns.map {
            it.mark(
                isSynced = false,
                isDeleted = false
            )
        }
    )

    private suspend fun delete(trns: List<Transaction>) = persist(
        trns = trns.map {
            it.mark(
                isSynced = false,
                isDeleted = true
            )
        }
    )

    private suspend fun persist(trns: List<Transaction>) =
        transactionDao.save(trns.map(::mapToEntity))
}