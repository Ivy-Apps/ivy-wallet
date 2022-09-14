package com.ivy.core.persistence.dao.trn

import androidx.room.Dao
import androidx.room.Query
import com.ivy.data.SyncState

@Dao
interface TrnLinkRecordDao {

    // region Update
    @Query("UPDATE trn_links SET sync = :sync WHERE trnId = :trnId")
    suspend fun updateSyncByTrnId(trnId: String, sync: SyncState)
    // endregion

}