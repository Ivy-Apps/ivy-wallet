package com.ivy.core.domain.action.transaction

import com.ivy.core.domain.action.data.Modify
import com.ivy.core.persistence.dao.trn.TrnLinkRecordDao
import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import com.ivy.data.SyncState
import com.ivy.data.transaction.TrnBatch
import com.ivy.frp.action.Action
import java.util.*
import javax.inject.Inject

/**
 * Saves or deletes a batch of transactions.
 *
 * Use:
 * - [WriteTrnsBatchAct.save]: to save a [TrnBatch]
 * - [WriteTrnsBatchAct.delete]: to delete a [TrnBatch]
 */
class WriteTrnsBatchAct @Inject constructor(
    private val writeTrnsAct: WriteTrnsAct,
    private val trnLinkRecordDao: TrnLinkRecordDao,
) : Action<WriteTrnsBatchAct.Input, Unit>() {
    companion object {
        fun save(batch: TrnBatch) = Input.Save(batch)
        fun delete(batch: TrnBatch) = Input.Delete(batch)
    }

    sealed interface Input {
        data class Save internal constructor(val batch: TrnBatch) : Input
        data class Delete internal constructor(val batch: TrnBatch) : Input
    }

    override suspend fun Input.willDo() {
        when (this) {
            is Input.Delete -> delete(batch)
            is Input.Save -> save(batch)
        }

        //Note: writeTrnsAct will notify of transactions update
    }

    private suspend fun delete(batch: TrnBatch) {
        val trnIds = batch.trns.map { it.id.toString() }
        writeTrnsAct(Modify.deleteMany(trnIds))
        trnLinkRecordDao.updateSyncByTrnIds(trnIds = trnIds, sync = SyncState.Deleting)
    }

    private suspend fun save(batch: TrnBatch) {
        writeTrnsAct(Modify.saveMany(batch.trns))

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