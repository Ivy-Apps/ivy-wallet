package com.ivy.core.persistence.dao.trn

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import com.ivy.data.DELETING
import com.ivy.data.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface TrnLinkRecordDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TrnLinkRecordEntity>)
    // endregion

    // region Select
    @Query("SELECT * FROM trn_links WHERE sync != $DELETING")
    suspend fun findAllBlocking(): List<TrnLinkRecordEntity>

    @Query("SELECT * FROM trn_links WHERE sync != $DELETING")
    fun findAll(): Flow<List<TrnLinkRecordEntity>>

    @Query("SELECT * FROM trn_links WHERE batchId = :batchId AND sync != $DELETING")
    suspend fun findByBatchId(batchId: String): List<TrnLinkRecordEntity>
    // endregion

    // region Update
    @Query("UPDATE trn_links SET sync = :sync WHERE trnId = :trnId")
    suspend fun updateSyncByTrnId(trnId: String, sync: SyncState)

    @Query("UPDATE trn_links SET sync = :sync WHERE trnId IN (:trnIds)")
    suspend fun updateSyncByTrnIds(trnIds: List<String>, sync: SyncState)
    // endregion

}