package com.ivy.core.persistence.dao.trn

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.core.persistence.entity.trn.TrnEntity

@Dao
interface TrnDao {
    // region Insert/Update
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: TrnEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TrnEntity>)
    // endregion

    @RawQuery
    suspend fun findBySQL(query: SupportSQLiteQuery): List<TrnEntity>

    // region Delete
    @Query("DELETE FROM transactions WHERE id IN (:ids)")
    suspend fun delete(ids: List<String>)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
    // endregion
}