package com.ivy.core.persistence.dao.trn

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import com.ivy.data.SyncState

@Dao
interface TrnLinkRecordDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TrnLinkRecordEntity>)
    // endregion

    // region Update
    @Query("UPDATE trn_links SET sync = :sync WHERE trnId = :trnId")
    suspend fun updateSyncByTrnId(trnId: String, sync: SyncState)

    @Query("UPDATE trn_links SET sync = :sync WHERE trnId IN (:trnIds)")
    suspend fun updateSyncByTrnIds(trnIds: List<String>, sync: SyncState)
    // endregion

}