package com.ivy.core.persistence.dao.trn

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.data.DELETING
import com.ivy.data.SyncState

@Dao
abstract class TransactionDao {
    // region Save
    @Upsert
    protected abstract suspend fun saveTrnEntity(entity: TransactionEntity)

    // region Tags
    @Query("UPDATE trn_tags SET sync = :sync WHERE trnId = :trnId")
    protected abstract suspend fun updateTrnTagsSyncByTrnId(trnId: String, sync: SyncState)

    @Upsert
    protected abstract suspend fun saveTags(entity: List<TrnTagEntity>)
    // endregion

    // region Attachments
    @Query("UPDATE attachments SET sync = :sync WHERE associatedId = :associatedId")
    protected abstract suspend fun updateAttachmentsSyncByAssociatedId(
        associatedId: String,
        sync: SyncState
    )

    @Upsert
    protected abstract suspend fun saveAttachments(entity: List<AttachmentEntity>)
    // endregion

    // region Metadata
    @Query("UPDATE trn_metadata SET sync = :sync WHERE trnId = :trnId")
    protected abstract suspend fun updateMetadataSyncByTrnId(trnId: String, sync: SyncState)

    @Upsert
    protected abstract suspend fun saveMetadata(entity: List<TrnMetadataEntity>)
    // endregion

    @Transaction
    open suspend fun saveMany(trns: List<SaveTrnData>) {
        trns.forEach { save(it) }
    }

    @Transaction
    open suspend fun save(data: SaveTrnData) {
        val trnId = data.entity.id
        saveTrnEntity(data.entity)

        // Delete existing tags
        updateTrnTagsSyncByTrnId(trnId, sync = SyncState.Deleting)
        saveTags(data.tags)

        // Delete existing attachments
        updateAttachmentsSyncByAssociatedId(trnId, sync = SyncState.Deleting)
        saveAttachments(data.attachments)

        // Delete existing metadata key-values
        updateMetadataSyncByTrnId(trnId, sync = SyncState.Deleting)
        saveMetadata(data.metadata)
    }
    // endregion

    // region Select
    @Query("SELECT * FROM transactions WHERE sync != $DELETING")
    abstract suspend fun findAllBlocking(): List<TransactionEntity>

    @RawQuery
    abstract suspend fun findBySQL(query: SupportSQLiteQuery): List<TransactionEntity>

    @Query(
        "SELECT accountId, time, timeType FROM transactions WHERE id = :trnId" +
                " AND sync = $DELETING LIMIT 1"
    )
    abstract suspend fun findAccountIdAndTimeById(trnId: String): AccountIdAndTrnTime?
    // endregion

    // region Delete
    @Query("UPDATE transactions SET sync = :sync WHERE id = :trnId")
    protected abstract suspend fun updateTrnEntitySyncById(trnId: String, sync: SyncState)

    @Transaction
    open suspend fun markDeletedMany(trnIds: List<String>) {
        trnIds.forEach { markDeleted(it) }
    }

    @Transaction
    open suspend fun markDeleted(trnId: String) {
        updateTrnEntitySyncById(trnId, sync = SyncState.Deleting)
        updateTrnTagsSyncByTrnId(trnId, sync = SyncState.Deleting)
        updateAttachmentsSyncByAssociatedId(trnId, sync = SyncState.Deleting)
        updateMetadataSyncByTrnId(trnId, sync = SyncState.Deleting)
    }
    // endregion

    @Transaction
    open suspend fun many(
        toSave: List<SaveTrnData>,
        toDeleteTrnIds: List<String>
    ) {
        markDeletedMany(trnIds = toDeleteTrnIds)
        saveMany(trns = toSave)
    }
}