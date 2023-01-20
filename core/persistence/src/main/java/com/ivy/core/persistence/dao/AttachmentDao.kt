package com.ivy.core.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.data.DELETING
import com.ivy.data.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: AttachmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(values: List<AttachmentEntity>)
    // endregion

    // region Select
    @Query("SELECT * FROM attachments WHERE sync != $DELETING")
    suspend fun findAllBlocking(): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE associatedId = :associatedId AND sync != $DELETING")
    fun findByAssociatedId(associatedId: String): Flow<List<AttachmentEntity>>
    // endregion


    // region Update
    @Query("UPDATE attachments SET sync = :sync WHERE associatedId = :associatedId")
    suspend fun updateSyncByAssociatedId(associatedId: String, sync: SyncState)
    // endregion
}