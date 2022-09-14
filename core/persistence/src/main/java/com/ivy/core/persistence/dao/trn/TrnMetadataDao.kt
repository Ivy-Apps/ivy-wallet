package com.ivy.core.persistence.dao.trn

import androidx.room.Dao
import androidx.room.Query
import com.ivy.data.SyncState

@Dao
interface TrnMetadataDao {

    // region Update
    @Query("UPDATE trn_metadata SET sync = :sync WHERE trnId = :trnId")
    suspend fun updateSyncByTrnId(trnId: String, sync: SyncState)
    // endregion
}