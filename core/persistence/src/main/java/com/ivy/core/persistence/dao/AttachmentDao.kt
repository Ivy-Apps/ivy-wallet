package com.ivy.core.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import com.ivy.data.SyncState

@Dao
interface AttachmentDao {

    // region Update
    @Query("UPDATE attachments SET sync = :sync WHERE associatedId = :associatedId")
    suspend fun updateSyncByAssociatedId(associatedId: String, sync: SyncState)
    // endregion
}