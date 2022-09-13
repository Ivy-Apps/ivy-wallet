package com.ivy.core.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.core.persistence.entity.trn.TrnEntity

@Dao
interface TrnDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: TrnEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TrnEntity>)

    // TODO: Just for testing
    @Query("SELECT * FROM transactions_v2")
    suspend fun findAll(): List<TrnEntity>
}