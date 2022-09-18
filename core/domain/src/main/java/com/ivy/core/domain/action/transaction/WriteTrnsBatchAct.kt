package com.ivy.core.domain.action.transaction

import com.ivy.core.domain.action.transaction.WriteTrnsAct.Companion.deleteMany
import com.ivy.core.domain.action.transaction.WriteTrnsAct.Companion.saveMany
import com.ivy.core.persistence.dao.trn.TrnLinkRecordDao
import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import com.ivy.data.SyncState
import com.ivy.data.transaction.TrnBatch
import com.ivy.frp.action.Action
import com.ivy.sync.SyncTask
import com.ivy.sync.syncTaskFrom
import java.util.*
import javax.inject.Inject

class WriteTrnsBatchAct @Inject constructor(
    private val writeTrnsAct: WriteTrnsAct,
    private val trnLinkRecordDao: TrnLinkRecordDao,
) : Action<WriteTrnsBatchAct.Input, SyncTask>() {
    companion object {
        fun save(batch: TrnBatch) = Input.Save(batch)
        fun delete(batch: TrnBatch) = Input.Delete(batch)
    }

    sealed interface Input {
        data class Save(val batch: TrnBatch) : Input
        data class Delete(val batch: TrnBatch) : Input
    }

    override suspend fun Input.willDo(): SyncTask {
        when (this) {
            is Input.Delete -> delete(batch)
            is Input.Save -> save(batch)
        }

        //writeTrnsAct will notify of update

        // TODO: Implement
        return syncTaskFrom {}
    }

    private suspend fun delete(batch: TrnBatch) {
        val trnIds = batch.trns.map { it.id.toString() }
        writeTrnsAct(deleteMany(trnIds))
        trnLinkRecordDao.updateSyncByTrnIds(trnIds = trnIds, sync = SyncState.Deleting)
    }

    private suspend fun save(batch: TrnBatch) {
        writeTrnsAct(saveMany(batch.trns))

        trnLinkRecordDao.save(
            batch.trns.map {
                TrnLinkRecordEntity(
                    id = UUID.randomUUID().toString(),
                    trnId = it.id.toString(),
                    batchId = batch.batchId,
                    sync = SyncState.Syncing,
                )
            }
        )
    }

}