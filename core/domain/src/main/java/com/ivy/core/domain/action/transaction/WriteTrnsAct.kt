package com.ivy.core.domain.action.transaction

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.pure.mapping.entity.mapToEntity
import com.ivy.core.domain.pure.mapping.entity.mapToTrnTagEntity
import com.ivy.core.domain.pure.transaction.validateTransaction
import com.ivy.core.domain.pure.util.beautify
import com.ivy.core.persistence.dao.AttachmentDao
import com.ivy.core.persistence.dao.trn.TrnDao
import com.ivy.core.persistence.dao.trn.TrnLinkRecordDao
import com.ivy.core.persistence.dao.trn.TrnMetadataDao
import com.ivy.core.persistence.dao.trn.TrnTagDao
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.data.SyncState.Deleting
import com.ivy.data.SyncState.Syncing
import com.ivy.data.attachment.Attachment
import com.ivy.data.tag.Tag
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnMetadata
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
    private val trnDao: TrnDao,
    private val trnsSignal: TrnsSignal,
    private val trnTagDao: TrnTagDao,
    private val trnLinkRecordDao: TrnLinkRecordDao,
    private val trnMetadataDao: TrnMetadataDao,
    private val attachmentDao: AttachmentDao,
    private val timeProvider: TimeProvider,
) : Action<Modify<Transaction>, Unit>() {

    override suspend fun Modify<Transaction>.willDo() {
        when (this) {
            is Modify.Save -> save(trns = items)
            is Modify.Delete -> delete(trnIds = itemIds)
        }

        trnsSignal.send(Unit) // notify for changed transactions
    }

    // region Save
    private suspend fun save(trns: List<Transaction>) = trns.forEach { saveTrn(it) }

    private suspend fun saveTrn(trn: Transaction) {
        if (!validateTransaction(trn)) return // don't save invalid transactions

        trnDao.save(
            mapToEntity(
                trn = trn.copy(
                    title = beautify(trn.title),
                    description = beautify(trn.description)
                ),
                timeProvider = timeProvider,
            ).copy(sync = Syncing)
        )

        // save associated data
        val trnId = trn.id.toString()
        saveTrnTags(trnId = trnId, tags = trn.tags)
        saveAttachments(trnId = trnId, attachments = trn.attachments)
        saveMetadata(trnId = trnId, metadata = trn.metadata)
    }

    private suspend fun saveTrnTags(trnId: String, tags: List<Tag>) {
        trnTagDao.updateSyncByTrnId(trnId = trnId, Deleting) // delete existing
        trnTagDao.save(tags.map {
            mapToTrnTagEntity(trnId = trnId, tagId = it.id, sync = Syncing)
        })
    }

    private suspend fun saveAttachments(trnId: String, attachments: List<Attachment>) {
        // delete existing
        attachmentDao.updateSyncByAssociatedId(
            associatedId = trnId, sync = Deleting
        )
        attachmentDao.save(attachments.map { mapToEntity(it).copy(sync = Syncing) })
    }

    private suspend fun saveMetadata(trnId: String, metadata: TrnMetadata) {
        fun newMetadata(
            key: String,
            value: String,
        ) = TrnMetadataEntity(
            id = UUID.randomUUID().toString(),
            trnId = trnId,
            key = key,
            value = value,
            sync = Syncing
        )

        suspend fun metadata(key: String, value: UUID?) = value?.toString()?.let {
            trnMetadataDao.save(newMetadata(key = key, value = it))
        }

        // delete existing
        trnMetadataDao.updateSyncByTrnId(trnId = trnId, sync = Deleting)
        metadata(key = TrnMetadata.RECURRING_RULE_ID, value = metadata.recurringRuleId)
        metadata(key = TrnMetadata.LOAN_ID, value = metadata.loanId)
        metadata(key = TrnMetadata.LOAN_RECORD_ID, value = metadata.loanRecordId)
    }
    // endregion

    // region Delete
    private suspend fun delete(trnIds: List<String>) = trnIds.forEach { deleteTrn(it) }

    private suspend fun deleteTrn(trnId: String) {
        trnDao.updateSyncById(trnId = trnId, sync = Deleting)

        // delete associated data
        attachmentDao.updateSyncByAssociatedId(associatedId = trnId, Deleting)
        trnTagDao.updateSyncByTrnId(trnId = trnId, Deleting)
        trnLinkRecordDao.updateSyncByTrnId(trnId = trnId, Deleting)
        trnMetadataDao.updateSyncByTrnId(trnId = trnId, Deleting)
    }
    // endregion
}