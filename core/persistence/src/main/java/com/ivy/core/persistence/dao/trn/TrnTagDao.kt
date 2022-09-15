package com.ivy.core.persistence.dao.trn

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.data.SyncState

@Dao
interface TrnTagDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: TrnTagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(values: List<TrnTagEntity>)
    // endregion


    // region Update
    @Query("UPDATE trn_tags SET sync = :sync WHERE trnId = :trnId")
    suspend fun updateSyncByTrnId(trnId: String, sync: SyncState)
    // endregion
}