package com.ivy.core.persistence.dao.trn

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.data.DELETING
import com.ivy.data.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface TrnMetadataDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: TrnMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TrnMetadataEntity>)
    // endregion

    // region Select
    @Query("SELECT * FROM trn_metadata WHERE sync != $DELETING")
    suspend fun findAllBlocking(): List<TrnMetadataEntity>

    @Query("SELECT * FROM trn_metadata WHERE trnId = :trnId AND sync != $DELETING")
    fun findByTrnId(trnId: String): Flow<List<TrnMetadataEntity>>
    // endregion

    // region Update
    @Query("UPDATE trn_metadata SET sync = :sync WHERE trnId = :trnId")
    suspend fun updateSyncByTrnId(trnId: String, sync: SyncState)
    // endregion
}