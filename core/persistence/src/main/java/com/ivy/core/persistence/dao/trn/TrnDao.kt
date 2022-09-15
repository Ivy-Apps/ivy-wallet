package com.ivy.core.persistence.dao.trn

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.core.persistence.entity.trn.TrnEntity
import com.ivy.data.SyncState

@Dao
interface TrnDao {
    // region Save
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: TrnEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TrnEntity>)
    // endregion

    // region Select
    @RawQuery
    suspend fun findBySQL(query: SupportSQLiteQuery): List<TrnEntity>
    // endregion

    // region Update
    @Query("UPDATE transactions SET sync = :sync WHERE id = :trnId")
    suspend fun updateSyncById(trnId: String, sync: SyncState)
    // endregion

    // region Delete
    @Query("DELETE FROM transactions WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
    // endregion
}