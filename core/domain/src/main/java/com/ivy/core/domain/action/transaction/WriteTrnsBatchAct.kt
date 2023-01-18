package com.ivy.core.domain.action.transaction

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toUtc
import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.trn.TrnLinkRecordDao
import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.transaction.TrnBatch
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
    private val timeProvider: TimeProvider,
) : Action<WriteTrnsBatchAct.ModifyBatch, Unit>() {
    companion object {
        fun save(batch: TrnBatch) = ModifyBatch.Save(batch)
        fun delete(batch: TrnBatch) = ModifyBatch.Delete(batch)
    }

    sealed interface ModifyBatch {
        data class Save internal constructor(val batch: TrnBatch) : ModifyBatch
        data class Delete internal constructor(val batch: TrnBatch) : ModifyBatch
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(modify: ModifyBatch) {
        when (modify) {
            is ModifyBatch.Delete -> delete(modify.batch)
            is ModifyBatch.Save -> save(modify.batch)
        }

        //Note: writeTrnsAct will notify of transactions update
    }

    private suspend fun delete(batch: TrnBatch) {
        val trnIds = batch.trns.map { it.id.toString() }
        batch.trns.forEach {
            // TODO: Might corrupt the cache
            writeTrnsAct(
                WriteTrnsAct.Input.Delete(
                    trnId = it.id.toString(),
                    affectedAccountIds = setOf(it.account.id.toString()),
                    originalTime = it.time,
                )
            )
        }
        trnLinkRecordDao.updateSyncByTrnIds(trnIds = trnIds, sync = SyncState.Deleting)
    }

    private suspend fun save(batch: TrnBatch) {
        batch.trns.map {
            it.copy(
                sync = Sync(
                    state = SyncState.Syncing,
                    lastUpdated = timeProvider.timeNow(),
                )
            )
        }.forEach {
            writeTrnsAct(WriteTrnsAct.Input.SaveInefficient(it))
        }


        trnLinkRecordDao.save(
            batch.trns.map {
                TrnLinkRecordEntity(
                    id = UUID.randomUUID().toString(),
                    trnId = it.id.toString(),
                    batchId = batch.batchId,
                    sync = SyncState.Syncing,
                    lastUpdated = timeProvider.timeNow().toUtc(timeProvider)
                )
            }
        )
    }

}