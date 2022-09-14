package com.ivy.core.domain.action.transaction

import com.ivy.core.persistence.dao.trn.TrnDao
import com.ivy.data.Modify
import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.sync.SyncTask
import com.ivy.sync.syncTaskFrom
import com.ivy.sync.transaction.mark
import com.ivy.temp.persistence.mapToEntity
import javax.inject.Inject

class WriteTrnsAct @Inject constructor(
    private val trnDao: TrnDao,
//    private val syncTrnsAct: SyncTrnsAct,
    private val trnsSignal: TrnsSignal
) : FPAction<Modify<Transaction>, SyncTask>() {
    override suspend fun Modify<Transaction>.compose(): suspend () -> SyncTask = {
        when (this) {
            is Modify.Save -> save(trns = item)
            is Modify.Delete -> delete(trns = item)
        }

        trnsSignal.send(Unit) // notify for changed transactions

        // TODO: Implement sync
        syncTaskFrom {}
    }

    private suspend fun save(trns: List<Transaction>) = persist(
        trnDao.save(
            trns.map { }
        )
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