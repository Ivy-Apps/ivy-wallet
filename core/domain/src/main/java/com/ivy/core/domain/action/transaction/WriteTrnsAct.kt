package com.ivy.core.domain.action.transaction

import arrow.core.*
import arrow.core.computations.option
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toLocal
import com.ivy.common.time.toUtc
import com.ivy.common.toNonEmptyList
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.core.domain.pure.mapping.entity.mapToEntity
import com.ivy.core.domain.pure.mapping.entity.mapToTrnTagEntity
import com.ivy.core.domain.pure.transaction.validateTransaction
import com.ivy.core.domain.pure.util.beautify
import com.ivy.core.persistence.IvyWalletCoreDb
import com.ivy.core.persistence.dao.trn.SaveTrnData
import com.ivy.core.persistence.dao.trn.TransactionDao
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.SyncState.Syncing
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnTime
import java.util.*
import javax.inject.Inject

/**
 * Persists transactions locally. Supports sync out-of-the-box.
 * See [Modify].
 *
 * ## Save transactions
 * ```
 * val writeTrnsAct: WriteTrnsAct // init via DI
 *
 * writeTrnsAct(Modify.save(trn)) // save a single transaction
 * writeTrnsAct(Modify.saveMany(trns)) // saves multiple transactions
 * ```
 * ## Delete transactions
 * ```
 * val writeTrnsAct: WriteTrnsAct // init via DI
 *
 * writeTrnsAct(Modify.delete(trn.id.toString())) // deletes a transaction
 * writeTrnsAct(Modify.deleteMany(trnIds.map { it.id.toString() })) // deletes multiple transactions
 * ```
 */
class WriteTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao,
    private val trnsSignal: TrnsSignal,
    private val timeProvider: TimeProvider,
    private val invalidateAccCacheAct: InvalidateAccCacheAct,
    private val db: IvyWalletCoreDb,
) : Action<WriteTrnsAct.Input, Unit>() {

    sealed interface Input {
        sealed interface Operation

        data class Delete(
            val trnId: String,
            val affectedAccountIds: Set<String>,
            val originalTime: TrnTime,
        ) : Input, Operation

        data class DeleteInefficient(
            val trnId: String
        ) : Input, Operation

        data class Update(
            val old: Transaction,
            val new: Transaction,
        ) : Input, Operation

        data class CreateNew(
            val trn: Transaction
        ) : Input, Operation

        data class SaveInefficient(
            val trn: Transaction
        ) : Input, Operation

        // TODO: Re-work this Many to be efficient
        data class ManyInefficient(
            val operations: List<Operation>
        ) : Input
    }

    override suspend fun action(input: Input) {
        when (input) {
            is Input.CreateNew -> createNew(input)
            is Input.Update -> update(input)
            is Input.Delete -> delete(input)
            is Input.DeleteInefficient -> deleteInefficient(input)
            is Input.SaveInefficient -> saveInefficient(input)
            is Input.ManyInefficient -> many(input)
        }

        trnsSignal.send(Unit) // notify for changed transactions
    }

    // region Operations
    private suspend fun createNew(input: Input.CreateNew) = option {
        val saveData = saveData(input.trn).bind()
        transactionDao.save(saveData)
        invalidateAccCacheAct(
            InvalidateAccCacheAct.Input.OnCreateTrn(
                time = input.trn.time,
                accountIds = input.trn.account.id.toString().nel()
            )
        )
    }

    private suspend fun update(input: Input.Update) = option {
        val saveData = saveData(input.new).bind()
        transactionDao.save(saveData)
        invalidateAccCacheAct(
            InvalidateAccCacheAct.Input.OnUpdateTrn(
                oldTime = input.old.time,
                time = input.new.time,
                accountIds = nonEmptyListOf(
                    input.old.account.id.toString(),
                    input.new.account.id.toString(),
                )
            )
        )
    }

    private suspend fun delete(input: Input.Delete) = option {
        transactionDao.markDeleted(input.trnId)
        invalidateAccCacheAct(
            InvalidateAccCacheAct.Input.OnDeleteTrn(
                time = input.originalTime,
                accountIds = input.affectedAccountIds.toNonEmptyList()
            )
        )
    }

    private suspend fun deleteInefficient(input: Input.DeleteInefficient) = option {
        val invalidateData = findInvalidateCacheData(input.trnId)
        transactionDao.markDeleted(input.trnId)
        invalidateData?.let {
            invalidateAccCacheAct(
                InvalidateAccCacheAct.Input.OnDeleteTrn(
                    time = it.time,
                    accountIds = it.accountId.nel()
                )
            )
        }
    }

    private suspend fun saveInefficient(input: Input.SaveInefficient) = option {
        val saveData = saveData(input.trn).bind()
        val trnExists = findInvalidateCacheData(input.trn.id.toString())
        transactionDao.save(saveData)
        invalidateAccCacheAct(
            if (trnExists != null) {
                InvalidateAccCacheAct.Input.OnUpdateTrn(
                    oldTime = trnExists.time,
                    time = input.trn.time,
                    accountIds = nonEmptyListOf(
                        trnExists.accountId,
                        input.trn.account.id.toString()
                    )
                )
            } else {
                InvalidateAccCacheAct.Input.OnCreateTrn(
                    time = input.trn.time,
                    accountIds = input.trn.account.id.toString().nel()
                )
            }
        )
    }
    // endregion

    // region Many
    private suspend fun many(input: Input.ManyInefficient) {
        val pairs = input.operations.map {
            when (it) {
                is Input.CreateNew -> {
                    saveData(it.trn) to null
                }
                is Input.SaveInefficient -> {
                    saveData(it.trn) to null
                }
                is Input.Update -> {
                    saveData(it.new) to null
                }
                is Input.Delete -> {
                    null to it.trnId
                }
                is Input.DeleteInefficient -> {
                    null to it.trnId
                }
            }
        }

        transactionDao.many(
            toSave = pairs.mapNotNull { it.first?.orNull() },
            toDeleteTrnIds = pairs.mapNotNull { it.second }
        )

        // Invalidate all account's cache
        db.accountCacheDao().deleteAll()
    }
    // endregion

    // region TrnSaveData
    private fun saveData(trn: Transaction): Option<SaveTrnData> {
        if (!validateTransaction(trn)) return None

        val trnEntity = mapToEntity(
            trn = trn.copy(
                title = beautify(trn.title),
                description = beautify(trn.description)
            ),
            timeProvider = timeProvider,
        ).copy(sync = Syncing)
        val trnId = trn.id.toString()
        val tags = trn.tags.map {
            mapToTrnTagEntity(
                trnId = trnId,
                tagId = it.id,
                sync = it.sync.copy(state = Syncing),
                timeProvider = timeProvider,
            )
        }
        val attachments = trn.attachments.map {
            mapToEntity(
                it,
                timeProvider = timeProvider
            ).copy(sync = Syncing)
        }
        val metadata = metadataEntities(trnId = trnId, metadata = trn.metadata)

        return SaveTrnData(
            entity = trnEntity,
            tags = tags,
            attachments = attachments,
            metadata = metadata
        ).some()
    }

    private fun metadataEntities(
        trnId: String, metadata: TrnMetadata
    ): List<TrnMetadataEntity> {
        fun newMetadata(
            key: String,
            value: String,
        ) = TrnMetadataEntity(
            id = UUID.randomUUID().toString(),
            trnId = trnId,
            key = key,
            value = value,
            sync = Syncing,
            lastUpdated = timeProvider.timeNow().toUtc(timeProvider)
        )

        fun metadata(key: String, value: UUID?): TrnMetadataEntity? =
            value?.toString()?.let {
                newMetadata(key = key, value = it)
            }

        return listOfNotNull(
            metadata(key = TrnMetadata.RECURRING_RULE_ID, value = metadata.recurringRuleId),
            metadata(key = TrnMetadata.LOAN_ID, value = metadata.loanId),
            metadata(key = TrnMetadata.LOAN_RECORD_ID, value = metadata.loanRecordId)
        )
    }
    // endregion

    private suspend fun findInvalidateCacheData(
        trnId: String
    ): InvalidateCacheData? = transactionDao.findAccountIdAndTimeById(trnId = trnId)?.let {
        InvalidateCacheData(
            accountId = it.accountId,
            time = when (it.timeType) {
                TrnTimeType.Actual -> TrnTime.Actual(it.time.toLocal(timeProvider))
                TrnTimeType.Due -> TrnTime.Due(it.time.toLocal(timeProvider))
            }
        )
    }

    data class InvalidateCacheData(
        val accountId: String,
        val time: TrnTime
    )
}